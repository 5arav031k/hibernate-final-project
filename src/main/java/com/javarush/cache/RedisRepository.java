package com.javarush.cache;

import com.google.gson.Gson;
import com.javarush.config.RedisConfig;
import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.UnifiedJedis;

@Slf4j
public class RedisRepository {
    private final TopKRepository topKRepository;
    private final Gson gson = new Gson();
    private final UnifiedJedis jedis = RedisConfig.getUnifiedJedis();

    private static final int THRESHOLD = 5;
    private static final String KEY = "topk:request";

    public RedisRepository() {
        topKRepository = new TopKRepository();
        topKRepository.initializeTopK(KEY, THRESHOLD);
    }

    public boolean exist(String name) {
        String cacheKey = "cache:" + name;
        return jedis.exists(cacheKey);
    }

    public <T> T get(String name, Class<T> classOfT) {
        String cacheKey = "cache:" + name;
        return gson.fromJson(jedis.get(cacheKey), classOfT);
    }

    public <T> void setIfFrequentlyUsed(String name, T data) {
        String cacheKey = "cache:" + name;

        topKRepository.addItem(KEY, name);

        long count = topKRepository.getCount(KEY, name);
        if (count >= THRESHOLD) {
            jedis.set(cacheKey, gson.toJson(data));
            log.info("request is frequently used, add in jedis");
        } else {
            log.info("request is not frequently used, skipping");
        }
    }
}