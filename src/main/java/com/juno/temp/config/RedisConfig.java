package com.juno.temp.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedisConfig {
    private final String host;
    private final String port;
    private static final String REDISSON_HOST_PREFIX = "redis://";

    public RedisConfig(
        final @Value("${spring.data.redis.host}") String host,
        final @Value("${spring.data.redis.port}") String port
    ) {
        this.host = host;
        this.port = port;
    }

    @Bean
    public RedissonClient redissonClient() {
        Config config = new Config();
        String address = REDISSON_HOST_PREFIX + host + ":" + port;

        config
            .useSingleServer()
            .setAddress(address);

        return Redisson.create(config);
    }
}
