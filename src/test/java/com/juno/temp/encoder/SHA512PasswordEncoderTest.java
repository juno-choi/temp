package com.juno.temp.encoder;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SHA512PasswordEncoderTest {

    @Test
    @DisplayName("SHA-512 암호화와 복호화에 성공한다")
    void matchesSuccess1() {
        //given
        String password = "password123";
        SHA512PasswordEncoder sha512PasswordEncoder = new SHA512PasswordEncoder();
        String encodedPassword = sha512PasswordEncoder.encode(password);

        //when
        boolean matches = sha512PasswordEncoder.matches(password, encodedPassword);

        //then
        Assertions.assertThat(matches).isTrue();
    }

    @Test
    @DisplayName("salt로 인해 같은 비밀번호도 다른 해시 결과값을 얻는다")
    void encodeSuccess1() {
        //given
        String password = "password123";
        SHA512PasswordEncoder sha512PasswordEncoder = new SHA512PasswordEncoder();

        //when
        String encodedPassword1 = sha512PasswordEncoder.encode(password);
        String encodedPassword2 = sha512PasswordEncoder.encode(password);

        boolean matches1 = sha512PasswordEncoder.matches(password, encodedPassword1);
        boolean matches2 = sha512PasswordEncoder.matches(password, encodedPassword1);

        //then
        Assertions.assertThat(encodedPassword1).isNotEqualTo(encodedPassword2);
        Assertions.assertThat(matches1).isTrue();
        Assertions.assertThat(matches2).isTrue();
    }
}