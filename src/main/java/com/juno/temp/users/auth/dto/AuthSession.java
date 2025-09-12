package com.juno.temp.users.auth.dto;

import lombok.*;

@Builder(access = AccessLevel.PRIVATE)
public record AuthSession (
    String accessToken,
    String refreshToken,
    String userAgent
) {
    public static AuthSession of(final String accessToken, final String refreshToken, final String userAgent) {
        return AuthSession.builder()
            .accessToken(accessToken)
            .refreshToken(refreshToken)
            .userAgent(userAgent)
            .build();
    }
}
