package io.klovers.server.domains.user.controllers;

import io.klovers.server.common.authorize.JwtUserDetailService;
import io.klovers.server.common.exceptions.ApiException;
import io.klovers.server.domains.user.models.dtos.ReqLoginDto;
import io.klovers.server.domains.user.models.dtos.ReqSignUpDto;
import io.klovers.server.domains.user.models.dtos.UserDto;
import io.klovers.server.domains.user.services.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.util.List;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final JwtUserDetailService userDetailService;

    @PostMapping("/signUp")
    public UserDto signUp(@RequestBody ReqSignUpDto data, HttpServletResponse res) throws UnsupportedEncodingException, UsernameNotFoundException {
        userService.signUp(data);

        return userDetailService.createJwtToken(
                ReqLoginDto.builder()
                        .username(data.getUsername())
                        .password(data.getPassword())
                        .build(),
                res);
    }

    @PostMapping("/login")
    public UserDto signIn(@RequestBody ReqLoginDto data, HttpServletResponse res) throws UnsupportedEncodingException, UsernameNotFoundException {
        return userDetailService.createJwtToken(data, res);
    }

    @PostMapping("/logout")
    public void logout(HttpServletRequest req, HttpServletResponse res, UserDto userDto) {
        userDetailService.signOut(userDto.getUsername(), req, res);
    }

    @GetMapping("/list")
    public List<UserDto> list(UserDto userDto) {
        return userService.list(userDto);
    }
}
