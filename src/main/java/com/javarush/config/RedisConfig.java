package com.javarush.config;

import redis.clients.jedis.UnifiedJedis;

public class RedisConfig {
    private static UnifiedJedis instance;

    private RedisConfig() {
    }

    public static UnifiedJedis getUnifiedJedis() {
        if (instance == null) {
            instance = new UnifiedJedis("redis://localhost:6379");
        }
        return instance;
    }
}
