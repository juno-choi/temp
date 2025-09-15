package com.juno.temp.users.auth.service;

import com.juno.temp.users.auth.dto.AuthParam;
import jakarta.servlet.http.HttpServletRequest;

public interface AuthService {
    void login(final AuthParam param, final HttpServletRequest request);
    void logout(final AuthParam param, final HttpServletRequest request);
    String gatewayFilter(final AuthParam param, final HttpServletRequest request);
    String refresh(String refreshToken, HttpServletRequest request);
}
