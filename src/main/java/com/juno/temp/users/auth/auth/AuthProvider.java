package com.juno.temp.users.auth.auth;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class AuthProvider {

    public String getAccessToken() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        return "access_token:%s".formatted(now.format(formatter));
    }

    public String getRefreshToken() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        return "refresh_token:%s".formatted(now.format(formatter));
    }
}
