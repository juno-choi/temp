package com.juno.temp.users.auth.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.juno.temp.users.auth.auth.AuthProvider;
import com.juno.temp.users.auth.dto.AuthParam;
import com.juno.temp.users.auth.dto.AuthSession;
import com.juno.temp.users.auth.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.redisson.api.RBucket;
import org.redisson.api.RSetCache;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.StringCodec;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class AuthServiceDefaultImpl implements AuthService {
    private final RedissonClient redissonClient;
    private final UserRepository userRepository;
    private final AuthProvider authProvider;
    private final ObjectMapper objectMapper;

    private static final long refreshExpirationPeriod = 30 * 60 * 1000;
    private static final String REFRESH_TOKEN_PREFIX = "R-REFRESH-TOKEN:";
    private static final String USER_NAME_PREFIX = "R-TOKEN-USERNAME:";
    private static final String REFRESH_TOKEN_BLACK_PREFIX = "R-REFRESH-TOKEN-BLACK";

    @SneakyThrows(JsonProcessingException.class)
    @Override
    public void login(AuthParam param, HttpServletRequest request) {
        final String username = userRepository.find(param.id());
        final String accessToken = authProvider.getAccessToken();
        final String refreshToken = authProvider.getRefreshToken();

        // 중복 로그인 체크
        RBucket<String> usernameBucket = redissonClient.getBucket(USER_NAME_PREFIX + username, StringCodec.INSTANCE);
        if (usernameBucket.get() != null) {

            String userNameBucketAsString = usernameBucket.get();
            AuthSession authSession = objectMapper.readValue(userNameBucketAsString, AuthSession.class);

            RSetCache<String> blackSet = redissonClient.getSetCache(REFRESH_TOKEN_BLACK_PREFIX, StringCodec.INSTANCE);
            blackSet.add(authSession.refreshToken(), refreshExpirationPeriod, TimeUnit.SECONDS);

            // 기존 토큰은 삭제
            final RBucket<String> refreshTokenBucket = redissonClient.getBucket(REFRESH_TOKEN_PREFIX + authSession.refreshToken());
            refreshTokenBucket.delete();

            // user name 함께 삭제
            RBucket<String> savedUsernameBucket = redissonClient.getBucket(USER_NAME_PREFIX + username, StringCodec.INSTANCE);
            savedUsernameBucket.delete();
        }

        final RBucket<String> refreshTokenBucket = redissonClient.getBucket(REFRESH_TOKEN_PREFIX + refreshToken, StringCodec.INSTANCE);
        refreshTokenBucket.set(username, Duration.ofMillis(refreshExpirationPeriod));

        // token-username mapping
        AuthSession authSession = AuthSession.of(accessToken, refreshToken);
        usernameBucket.set(objectMapper.writeValueAsString(authSession), Duration.ofMillis(refreshExpirationPeriod));
    }

    @Override
    public void logout(AuthParam param, HttpServletRequest request) {
        String token = param.token();
        final String username = userRepository.find(param.id());

        final RBucket<String> refreshTokenBucket = redissonClient.getBucket(REFRESH_TOKEN_PREFIX + token);
        refreshTokenBucket.delete();

        // user name 함께 삭제
        RBucket<String> usernameBucket = redissonClient.getBucket(USER_NAME_PREFIX + username, StringCodec.INSTANCE);
        usernameBucket.delete();
    }

    @Override
    public String gatewayFilter(AuthParam param, HttpServletRequest request) {
        // 중복 로그인은 막아야 함
        String token = param.token();
        RSetCache<String> blackSet = redissonClient.getSetCache(REFRESH_TOKEN_BLACK_PREFIX, StringCodec.INSTANCE);

        if (blackSet.contains(token)) {
            return "duplicate login";
        }

        return "ok";
    }

    @Override
    public String refresh(String refreshToken, HttpServletRequest request) {
        RBucket<String> refreshTokenBucket = redissonClient.getBucket(REFRESH_TOKEN_PREFIX + refreshToken, StringCodec.INSTANCE);

        if (refreshTokenBucket.get() == null) {
            return "no refresh token";
        }

        return "refresh ok";
    }
}
