package com.juno.temp.users.auth.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.juno.temp.users.auth.auth.AuthProvider;
import com.juno.temp.users.auth.dto.AuthParam;
import com.juno.temp.users.auth.dto.AuthSession;
import com.juno.temp.users.auth.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RMapCache;
import org.redisson.api.RSetCache;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.StringCodec;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {
    private final RedissonClient redissonClient;
    private final UserRepository userRepository;
    private final AuthProvider authProvider;
    private final ObjectMapper objectMapper;

    final int ACCESS_TOKEN_EXPIRE_MINUTE = 30;
    final int REFRESH_TOKEN_EXPIRE_MINUTE = 180;
    final String SESSION_PREFIX = "USER:SESSION";
    final String BLACK_LIST_ACCESS_TOKEN_PREFIX = "USER:ACCESS_TOKEN:BLACKLIST";
    final String BLACK_LIST_REFRESH_TOKEN_PREFIX = "USER:REFRESH_TOKEN:BLACKLIST";

    @Override
    public void login(final AuthParam param, final HttpServletRequest request) {

        // 로그인 하는 유저의 user-agent와 함께 user-name을 저장한다.
        final String findUuid = userRepository.find(param.id());
        final String sessionKey = "%s:%s".formatted(SESSION_PREFIX, findUuid);

        final String accessToken = authProvider.getAccessToken();
        final String refreshToken = authProvider.getRefreshToken();
        final String userAgent = request.getHeader("user-agent");

        final AuthSession authSession = AuthSession.of(accessToken, refreshToken, userAgent);

        RMapCache<String, String> sessionMapCache = redissonClient.getMapCache(SESSION_PREFIX, StringCodec.INSTANCE);
        String redisSession = sessionMapCache.get(sessionKey);

        if (isAlreadyLogin(redisSession)) {
            log.info("token black process");
            RSetCache<String> blackAccessTokenSetCache = redissonClient.getSetCache(BLACK_LIST_ACCESS_TOKEN_PREFIX, StringCodec.INSTANCE);
            RSetCache<String> blackRefreshTokenSetCache = redissonClient.getSetCache(BLACK_LIST_REFRESH_TOKEN_PREFIX, StringCodec.INSTANCE);

            AuthSession existAuthSession = null;
            try {
                existAuthSession = objectMapper.readValue(redisSession, AuthSession.class);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }

            blackAccessTokenSetCache.add(existAuthSession.accessToken(), ACCESS_TOKEN_EXPIRE_MINUTE, TimeUnit.MINUTES);
            blackRefreshTokenSetCache.add(existAuthSession.refreshToken(), REFRESH_TOKEN_EXPIRE_MINUTE, TimeUnit.MINUTES);
        }

        String sessionObjectAsString = "";
        try {
            sessionObjectAsString = objectMapper.writeValueAsString(authSession);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        sessionMapCache.put(sessionKey, sessionObjectAsString);
    }

    private static boolean isAlreadyLogin(Object redisSession) {
        return redisSession != null;
    }

    @Override
    public String gatewayFilter(final AuthParam param, HttpServletRequest request) {
        String accessToken = param.token();
        // access token parse logic...
        // black process
        RSetCache<String> blackAccessTokenSetCache = redissonClient.getSetCache(BLACK_LIST_ACCESS_TOKEN_PREFIX, StringCodec.INSTANCE);

        if (blackAccessTokenSetCache.contains(accessToken)) {
            return "access black list";
        }

        return "access ok";
    }

    @Override
    public String refresh(String refreshToken, HttpServletRequest request) {

        // access token parse logic...
        // black process
        RSetCache<String> blackRefreshTokenSetCache = redissonClient.getSetCache(BLACK_LIST_REFRESH_TOKEN_PREFIX, StringCodec.INSTANCE);

        if (blackRefreshTokenSetCache.contains(refreshToken)) {
            return "refresh black list";
        }

        return "refresh ok";
    }
}
