package com.juno.temp.encoder;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.regex.Pattern;

public class SHA512PasswordEncoder implements PasswordEncoder {

    private final SecureRandom random = new SecureRandom();
    private final Log logger = LogFactory.getLog(getClass());
    private final String ALGORITHM = "SHA-512";
    private final int SALT_LENGTH = 16;
    private final int ITERATIONS = 100_000;
    private final Pattern SHA512_PATTERN = Pattern.compile("^\\$sha512\\$\\d+\\$[A-Za-z0-9+/=]+\\$[A-Za-z0-9+/=]+$");

    @Override
    public String encode(final CharSequence rawPassword) {
        try {
            Base64.Encoder base64Encoder = Base64.getEncoder();

            // 레인보우 테이블 공격에 대비하여 랜덤한 salt를 만든다.
            final byte[] saltByte = generateSalt();
            String salt = base64Encoder.encodeToString(saltByte);

            // 브루트 포스 공격을 대비하여 암호화 반복을 통해 충분히 느리게 만든다. 10ms ~ 200ms 추천
            final byte[] hashByte = digest(rawPassword.toString(), saltByte, ITERATIONS);
            String hash = base64Encoder.encodeToString(hashByte);
            
            // bcrypt처럼 최종 문자열에 알고리즘/파라미터/결과 다 넣기
            return "$sha512$%d$%s$%s".formatted(ITERATIONS, salt, hash);

        } catch (final Exception e) {
            throw new IllegalStateException("SHA-512 encoding failed", e);
        }
    }

    private byte[] generateSalt() {
        // bcrypt는 salt 자동 생성 → 여기서는 랜덤 16바이트 salt
        final byte[] saltByte = new byte[SALT_LENGTH];
        random.nextBytes(saltByte);
        return saltByte;
    }

    @Override
    public boolean matches(final CharSequence rawPassword, final String encodedPassword) {
        if (rawPassword == null) {
            throw new IllegalArgumentException("rawPassword cannot be null");
        }
        if (encodedPassword == null || encodedPassword.isEmpty()) {
            this.logger.warn("Empty encoded password");
            return false;
        }
        if (!this.SHA512_PATTERN.matcher(encodedPassword).matches()) {
            this.logger.warn("Encoded password does not look like SHA-512");
            return false;
        }

        return checkpw(rawPassword, encodedPassword);
    }

    private boolean checkpw(CharSequence rawPassword, String encodedPassword) {
        try {
            // 저장된 문자열 파싱
            final String[] parts = encodedPassword.split("\\$");
            final int iterations = Integer.parseInt(parts[2]);
            final byte[] salt = Base64.getDecoder().decode(parts[3]);
            final byte[] expectedHash = Base64.getDecoder().decode(parts[4]);

            // 입력값 해싱
            final byte[] actualHash = digest(rawPassword.toString(), salt, iterations);

            return MessageDigest.isEqual(expectedHash, actualHash);
        } catch (final Exception e) {
            return false;
        }
    }

    private byte[] digest(final String password, final byte[] salt, final int iterations) throws Exception {
        final MessageDigest md = MessageDigest.getInstance(ALGORITHM);
        byte[] result = concat(password.getBytes(), salt);

        // 일부러 느리게 적용하기 위해 iterations 만큼 해시를 반복시킴
        for (int i = 0; i < iterations; i++) {
            result = md.digest(result);
        }

        return result;
    }

    private byte[] concat(final byte[] a, final byte[] b) {
        final byte[] result = new byte[a.length + b.length];
        System.arraycopy(a, 0, result, 0, a.length);
        System.arraycopy(b, 0, result, a.length, b.length);
        return result;
    }
}
