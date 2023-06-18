package io.klovers.server.common.authorize.services;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.klovers.server.common.exceptions.ApiException;
import io.klovers.server.common.utils.AuthUtils;
import io.klovers.server.common.utils.CookieUtils;
import io.klovers.server.common.utils.RedisUtils;
import io.klovers.server.domains.user.models.dtos.ReqLoginDto;
import io.klovers.server.domains.user.models.dtos.UserDto;
import io.klovers.server.domains.user.repositories.UserRepo;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.util.WebUtils;

import java.io.UnsupportedEncodingException;
import java.util.*;

@Component
public class JwtUserDetailService implements UserDetailsService {

    @Value("${jwt.refresh_expiration}")
    private String refreshExpirationTime;
    @Value("${jwt.expiration}")
    private String expirationTime;
    @Value("${front_domain}")
    private String frontDomain;
    private final String ACCESS_TOKEN = "access_token";
    private final String REFRESH_TOKEN = "refresh_token";
    private final String SIGNED = "signed";
    private final String UNSIGNED = "unsigned";
    private final Logger log = LoggerFactory.getLogger(JwtUserDetailService.class);
    private final UserRepo userRepo;
    private final AuthUtils authUtils;
    private final AuthenticationManager authenticationManagerBean;
    private final JwtUserDetailService userDetailService;
    private final RedisUtils redisUtils;

    public JwtUserDetailService(UserRepo userRepo, AuthUtils authUtils, @Lazy AuthenticationManager authenticationManagerBean, @Lazy JwtUserDetailService userDetailService, RedisUtils redisUtils) {
        this.userRepo = userRepo;
        this.authUtils = authUtils;
        this.authenticationManagerBean = authenticationManagerBean;
        this.userDetailService = userDetailService;
        this.redisUtils = redisUtils;
    }

    public UserDto createJwtToken(ReqLoginDto data, HttpServletResponse res) throws ApiException, UnsupportedEncodingException, UsernameNotFoundException {
        String username = data.getUsername();
        String password = data.getPassword();
        // 아이디 패스워드 체크
        authenticate(username, password);

        final UserDetails userDetails = loadUserByUsername(username);
        String ATK = authUtils.generate(userDetails, ACCESS_TOKEN);
        String RTK = authUtils.generate(userDetails, REFRESH_TOKEN);
        CookieUtils.create(res, ACCESS_TOKEN, ATK, true, false, Integer.parseInt(expirationTime), frontDomain); // maxAge 1시간
        CookieUtils.create(res, REFRESH_TOKEN, RTK, true, false, Integer.parseInt(refreshExpirationTime), frontDomain); // maxAge 30일

        redisUtils.setData(username + "_atk", ATK, Long.parseLong(expirationTime));
        redisUtils.setData(username + "_rtk", RTK, Long.parseLong(refreshExpirationTime));

        return createUserDto(username);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserDto user = userRepo
                .findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Username is not valid"))
                .toDto();

        return new org.springframework.security.core.userdetails.User(Objects.toString(user.getId()), null, getAuthorities(user));
    }

    public UserDetails loadUserByToken(String token) {
        Claims claims = authUtils.getAllClaimsFromToken(token);
        String username = authUtils.getUserNameFromToken(token);
        String password = claims.get("password", String.class);
        Set<SimpleGrantedAuthority> authorities = new HashSet<>();
        // [{authority=ROLE_MASTER}] -> ROLE_MASTER
        String role = claims.get("role").toString().replace("[{authority=", "").replace("}]", "");
        authorities.add(new SimpleGrantedAuthority(role));
        return new org.springframework.security.core.userdetails.User(username, password, authorities);
    }

    private Set<SimpleGrantedAuthority> getAuthorities(UserDto user) {
        Set<SimpleGrantedAuthority> authorities = new HashSet<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_" + user.getRole().getValue()));
        return authorities;
    }

    private void authenticate(String userName, String userPassword) throws ApiException {
        try {
            authenticationManagerBean.authenticate(new UsernamePasswordAuthenticationToken(userName, userPassword));
        } catch (DisabledException | BadCredentialsException e) {
            throw new ApiException("아이디 패스워드를 다시한번 확인해주세요");
        }
    }

    private UserDto createUserDto(String username) throws ApiException {
        /* if (UserDto.getWithdrawn())
                    throw new ApiException("이미 탈퇴한 계정 입니다");
                if (!UserDto.getActive())
                    throw new ApiException("아직 승인되지 않은 계정입니다"); */
        return userRepo
                .findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Username is not valid"))
                .toDto();
    }

    public UserDto autoSignIn(HttpServletRequest req) throws ApiException{
        final String header = req.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            String jwtToken = header.substring(7);
            try {
                String userName = authUtils.getUserNameFromToken(jwtToken);
                return createUserDto(userName);
            } catch (IllegalArgumentException e) {
                log.debug("Unable to get JWT token");
            } catch (ExpiredJwtException e) {
                // token 의 유효기간이 지난경우
                log.debug("JWT token expired");
            }
        } else {
            log.debug("Jwt token does not start with Bearer");
        }
        return null;
    }

    public String jwt (HttpServletRequest req, HttpServletResponse res) {
        Cookie ATKCookie = WebUtils.getCookie(req, ACCESS_TOKEN);
        if (ATKCookie != null && redisValidateAtk(ATKCookie) && authUtils.validateToken(ATKCookie.getValue())) {
            return SIGNED;
        } else {
            return refresh(req, res);
        }
    }

    private String refresh (HttpServletRequest req, HttpServletResponse res) {
        Cookie RTKCookie = WebUtils.getCookie(req, REFRESH_TOKEN);
        if (RTKCookie == null) {
            return UNSIGNED;
        }
        try {
            String RTK = RTKCookie.getValue();
            String username = authUtils.getUserNameFromToken(RTK);
            UserDetails userDetails = userDetailService.loadUserByUsername(username);
            if (redisValidateRtk(RTKCookie) && authUtils.validateToken(RTK)) {
                String ATK = authUtils.generate(userDetails, ACCESS_TOKEN);
                CookieUtils.create(res, ACCESS_TOKEN, ATK, true, false, Integer.parseInt(expirationTime), frontDomain);
                redisUtils.setData(username + "_atk", ATK, Long.parseLong(expirationTime));

                // RTK 만료 7일 미만으로 남았을 경우 RTK 도 재발급
                Date expDate = authUtils.getExpirationDateFromToken(RTK);
                Date now = new Date();
                int dateDiff = (int) (now.getTime() - expDate.getTime()) / (1000 * 60 * 60 * 24);
                if (dateDiff <= 7) {
                    String RTK2 = authUtils.generate(userDetails, REFRESH_TOKEN);
                    CookieUtils.create(res, REFRESH_TOKEN, RTK2, true, false, Integer.parseInt(refreshExpirationTime), frontDomain); // maxAge 30일
                    redisUtils.deleteData(username + "_rtk");
                    redisUtils.setData(username + "_rtk", RTK2, Long.parseLong(refreshExpirationTime));
                }
                return SIGNED;
            } else {
                return UNSIGNED;
            }
        } catch (Exception e) {
            return UNSIGNED;
        }
    }

    public boolean redisValidateAtk (Cookie atkCookie) {
        if (atkCookie == null) {
            return false;
        }
        String username = authUtils.getUserNameFromToken(atkCookie.getValue());
        String redisToken = redisUtils.getData(username + "_atk");

        return redisToken != null;
    }

    public boolean redisValidateRtk (Cookie atkCookie) {
        if (atkCookie == null) {
            return false;
        }
        String username = authUtils.getUserNameFromToken(atkCookie.getValue());
        String redisToken = redisUtils.getData(username + "_rtk");

        return redisToken != null;
    }

    public void signOut(String username, HttpServletRequest req, HttpServletResponse res) {
        // 쿠기 삭제
        removeJwtCookies(res);

        // redis 데이터 삭제
        Cookie ATKCookie = WebUtils.getCookie(req, ACCESS_TOKEN);
        if (ATKCookie == null) { return; }
        String usernameFromToken = authUtils.getUserNameFromToken(ATKCookie.getValue());
        if (username != null && username.equals(usernameFromToken)) {
            redisUtils.deleteData(username + "_atk");
            redisUtils.deleteData(username + "_rtk");
        }
    }

    private void removeJwtCookies (HttpServletResponse res) {
        Cookie atk = new Cookie(ACCESS_TOKEN, null);
        Cookie rtk = new Cookie(REFRESH_TOKEN, null);

        atk.setMaxAge(0); // 쿠키의 expiration 타임을 0으로 하여 없앤다.
        rtk.setMaxAge(0);
        atk.setPath("/"); // 모든 경로에서 삭제 됬음을 알린다.
        rtk.setPath("/");

        res.addCookie(atk);
        res.addCookie(rtk);
    }
}
