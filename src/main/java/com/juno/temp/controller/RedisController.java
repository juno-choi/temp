package com.juno.temp.controller;

import lombok.RequiredArgsConstructor;
import org.redisson.api.RMap;
import org.redisson.api.RMapCache;
import org.redisson.api.RedissonClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

@RestController
@RequiredArgsConstructor
public class RedisController {
    private final RedissonClient redissonClient;

    @GetMapping("/redis/{key}")
    public String redis(@PathVariable String key) {
        redissonClient.getMap("BTC").put("USD", key);
        return "ok";
    }

    @GetMapping("/redis/ttl/1")
    public String redisTtl1() {
        RMap<Object, Object> rmap = redissonClient.getMap("BTC");
        rmap.put("USD321", 10000);
        rmap.expire(10, TimeUnit.SECONDS);
        return "ok";
    }

    @GetMapping("/redis/ttl/2")
    public String redisTtl2() {
        RMapCache<Object, Object> rmap = redissonClient.getMapCache("BTC");
        rmap.put("USD123", 12000, 10, TimeUnit.SECONDS);
        return "ok";
    }
}
