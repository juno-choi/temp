package com.juno.temp.encoder;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder;

import java.util.Map;

import static org.assertj.core.api.Assertions.*;

class SHA512PasswordEncoderTest {
    final Pbkdf2PasswordEncoder pbkdf2PasswordEncoder = new Pbkdf2PasswordEncoder("", 16, 310_000, Pbkdf2PasswordEncoder.SecretKeyFactoryAlgorithm.PBKDF2WithHmacSHA512);
    final BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
    final Map<String, PasswordEncoder> encoders = Map.of("v1", bCryptPasswordEncoder, "v2", pbkdf2PasswordEncoder);
    final DelegatingPasswordEncoder passwordEncoder = new DelegatingPasswordEncoder("v2", encoders);

    @Test
    @DisplayName("SHA-512 암호화와 복호화에 성공한다")
    void matchesSuccess1() {
        //given
        String password = "password123";
        DelegatingPasswordEncoder passwordEncoders = passwordEncoder;
        String encodedPassword = passwordEncoders.encode(password);

        //when
        boolean matches = passwordEncoders.matches(password, encodedPassword);

        //then
        assertThat(matches).isTrue();
    }

    @Test
    @DisplayName("salt로 인해 같은 비밀번호도 다른 해시 결과값을 얻는다")
    void encodeSuccess1() {
        //given
        String password = "password123";
        Pbkdf2PasswordEncoder sha512PasswordEncoder = new Pbkdf2PasswordEncoder("", 16, 1_000, Pbkdf2PasswordEncoder.SecretKeyFactoryAlgorithm.PBKDF2WithHmacSHA512);

        //when
        String encodedPassword1 = sha512PasswordEncoder.encode(password);
        String encodedPassword2 = sha512PasswordEncoder.encode(password);

        boolean matches1 = sha512PasswordEncoder.matches(password, encodedPassword1);
        boolean matches2 = sha512PasswordEncoder.matches(password, encodedPassword1);

        //then
        assertThat(encodedPassword1).isNotEqualTo(encodedPassword2);
        assertThat(matches1).isTrue();
        assertThat(matches2).isTrue();
    }

    @Test
    @DisplayName("BCrypt로 암호화된 데이터와 SHA-512로 암호화된 데이터를 구분해 낸다")
    void encodeSuccess2() {
        //given
        String password = "password123";
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        PasswordEncoder sha512PasswordEncoder = passwordEncoder;
        String bcryptPrefix = "$2a";

        String bcryptEncodePassword = bCryptPasswordEncoder.encode(password);
        String sha512EncodePassword = sha512PasswordEncoder.encode(password);

        //when
        boolean isBcrypt = bcryptEncodePassword.contains(bcryptPrefix);

        //then
        assertThat(bcryptEncodePassword).isNotEqualTo(sha512EncodePassword);
        assertThat(isBcrypt).isTrue();
    }

    @Test
    @DisplayName("Pbkdf2PasswordEncoder encoding")
    void pbkdf2PasswordEncoder() {
        //given
        String password = "password123";
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        PasswordEncoder sha512PasswordEncoder = passwordEncoder;
        String bcryptPrefix = "{v1}";

        String bcryptEncodePassword = bcryptPrefix+bCryptPasswordEncoder.encode(password);

        //when
        boolean matches = passwordEncoder.matches(password, bcryptEncodePassword);

        //then
        Assertions.assertThat(matches).isTrue();
    }
}