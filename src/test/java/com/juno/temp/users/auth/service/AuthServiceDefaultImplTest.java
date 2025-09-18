package com.juno.temp.users.auth.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

class AuthServiceDefaultImplTest {

    @Test
    @DisplayName("90일 테스트")
    void test() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime today = LocalDateTime.of(2025, 12, 14, 0, 0, 0);
        LocalDateTime today2 = LocalDateTime.of(2025, 12, 15, 0, 0, 0);
        LocalDateTime target = now.plusDays(90L);

        boolean after = today.isAfter(target);
        boolean after2 = today2.isAfter(target);

        System.out.println("test");
    }
}