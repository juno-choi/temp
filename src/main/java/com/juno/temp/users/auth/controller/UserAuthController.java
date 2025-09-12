package com.juno.temp.users.auth.controller;

import com.juno.temp.users.auth.dto.AuthParam;
import com.juno.temp.users.auth.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class UserAuthController {
    private final AuthService authService;

    @PostMapping("/login")
    public void login(@RequestBody final AuthParam param, final HttpServletRequest request) {
        authService.login(param, request);
    }

    @PostMapping("/gateway-filter")
    public String gatewayFilter(@RequestBody final AuthParam param, final HttpServletRequest request) {
        return authService.gatewayFilter(param, request);
    }

    @GetMapping("/refresh/{refreshToken}")
    public String refresh(@PathVariable final String refreshToken, final HttpServletRequest request) {
        return authService.refresh(refreshToken, request);
    }
}
