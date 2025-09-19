package com.juno.temp.encoder;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.assertj.core.api.Assertions.*;

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
        assertThat(matches).isTrue();
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
        SHA512PasswordEncoder sha512PasswordEncoder = new SHA512PasswordEncoder();
        String bcryptPrefix = "$2a";
        String sha512Prefix = "$sha512$";

        String bcryptEncodePassword = bCryptPasswordEncoder.encode(password);
        String sha512EncodePassword = sha512PasswordEncoder.encode(password);

        //when
        boolean isBcrypt = bcryptEncodePassword.contains(bcryptPrefix);
        boolean isSha512 = sha512EncodePassword.contains(sha512Prefix);

        //then
        assertThat(bcryptEncodePassword).isNotEqualTo(sha512EncodePassword);
        assertThat(isBcrypt).isTrue();
        assertThat(isSha512).isTrue();
    }
}