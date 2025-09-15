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
    final String BLACK_REASON_DUPLICATE = "duplicate";
    final String BLACK_REASON_LOGOUT = "logout";

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
            RMapCache<String, String> blackAccessTokenMapCache = redissonClient.getMapCache(BLACK_LIST_ACCESS_TOKEN_PREFIX, StringCodec.INSTANCE);
            RMapCache<String, String> blackRefreshTokenMapCache = redissonClient.getMapCache(BLACK_LIST_REFRESH_TOKEN_PREFIX, StringCodec.INSTANCE);

            AuthSession existAuthSession = null;
            try {
                existAuthSession = objectMapper.readValue(redisSession, AuthSession.class);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }

            blackAccessTokenMapCache.put(existAuthSession.accessToken(), BLACK_REASON_DUPLICATE, ACCESS_TOKEN_EXPIRE_MINUTE, TimeUnit.MINUTES);
            blackRefreshTokenMapCache.put(existAuthSession.refreshToken(), BLACK_REASON_DUPLICATE, REFRESH_TOKEN_EXPIRE_MINUTE, TimeUnit.MINUTES);
        }

        String sessionObjectAsString = "";
        try {
            sessionObjectAsString = objectMapper.writeValueAsString(authSession);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        sessionMapCache.put(sessionKey, sessionObjectAsString);
    }


    @Override
    public void logout(AuthParam param, HttpServletRequest request) {
        final String findUuid = userRepository.find(param.id());
        final String sessionKey = "%s:%s".formatted(SESSION_PREFIX, findUuid);
        RMapCache<String, String> sessionMapCache = redissonClient.getMapCache(SESSION_PREFIX, StringCodec.INSTANCE);
        String sessionAsString = sessionMapCache.get(sessionKey);
        AuthSession authSession = null;
        try {
            authSession = objectMapper.readValue(sessionAsString, AuthSession.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        sessionMapCache.remove(sessionKey);

        RMapCache<String, String> blackAccessTokenMapCache = redissonClient.getMapCache(BLACK_LIST_ACCESS_TOKEN_PREFIX, StringCodec.INSTANCE);
        RMapCache<String, String> blackRefreshTokenMapCache = redissonClient.getMapCache(BLACK_LIST_REFRESH_TOKEN_PREFIX, StringCodec.INSTANCE);
        blackAccessTokenMapCache.put(authSession.accessToken(), BLACK_REASON_LOGOUT, ACCESS_TOKEN_EXPIRE_MINUTE, TimeUnit.MINUTES);
        blackRefreshTokenMapCache.put(authSession.refreshToken(), BLACK_REASON_LOGOUT, REFRESH_TOKEN_EXPIRE_MINUTE, TimeUnit.MINUTES);
    }

    private static boolean isAlreadyLogin(Object redisSession) {
        return redisSession != null;
    }

    @Override
    public String gatewayFilter(final AuthParam param, HttpServletRequest request) {
        String accessToken = param.token();
        // access token parse logic...
        // black process
        RMapCache<String, String> blackAccessTokenMapCache = redissonClient.getMapCache(BLACK_LIST_ACCESS_TOKEN_PREFIX, StringCodec.INSTANCE);
        blackAccessTokenMapCache.get(accessToken);

        if (blackAccessTokenMapCache.get(accessToken) != null) {
            return blackAccessTokenMapCache.get(accessToken);
        }

        return "access ok";
    }

    @Override
    public String refresh(String refreshToken, HttpServletRequest request) {
        // access token parse logic...
        // black process
        RMapCache<String, String> blackRefreshTokenMapCache = redissonClient.getMapCache(BLACK_LIST_REFRESH_TOKEN_PREFIX, StringCodec.INSTANCE);

        if (blackRefreshTokenMapCache.get(refreshToken) != null) {
            return blackRefreshTokenMapCache.get(refreshToken);
        }

        return "refresh ok";
    }
}
