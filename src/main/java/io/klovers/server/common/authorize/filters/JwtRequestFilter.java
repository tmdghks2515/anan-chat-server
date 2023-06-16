package io.klovers.server.common.authorize.filters;

import com.auth0.jwt.exceptions.TokenExpiredException;
import io.jsonwebtoken.ExpiredJwtException;
import io.klovers.server.common.authorize.services.JwtUserDetailService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.WebUtils;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtRequestFilter extends OncePerRequestFilter {
    private final JwtUserDetailService userDetailService;
    private final Logger log = LoggerFactory.getLogger(JwtRequestFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain filterChain) throws ServletException, IOException {
        String REFRESH_TOKEN = "refresh_token";
        String SIGNED = "signed";
        try {
            String result = userDetailService.jwt(req, res);
            if (SIGNED.equals(result) && SecurityContextHolder.getContext().getAuthentication() == null) {
                Cookie cookie = WebUtils.getCookie(req, REFRESH_TOKEN);
                if (cookie != null) {
                    String RTK = cookie.getValue();
                    UserDetails userDetails = userDetailService.loadUserByToken(RTK);
                    UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(req));
                    SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
                }
            }
            // 인증 처리 후 정상적으로 다음 Filter 수행
            filterChain.doFilter(req, res);
        } catch (ExpiredJwtException | TokenExpiredException e) {
            log.info("Access Token 이 만료되었습니다.");
            filterChain.doFilter(req, res);
        } catch (Exception e) {
            log.debug("Unable to get JWT token");
            filterChain.doFilter(req, res);
        }
    }
}
