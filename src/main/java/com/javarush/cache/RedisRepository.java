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
    private static final String CACHE_KEY = "cache:";

    public RedisRepository() {
        topKRepository = new TopKRepository();
        topKRepository.initializeTopK(KEY, THRESHOLD);
    }

    public boolean exist(String name) {
        return jedis.exists(CACHE_KEY + name);
    }

    public <T> T get(String name, Class<T> classOfT) {
        return gson.fromJson(jedis.get(CACHE_KEY + name), classOfT);
    }

    public <T> void setIfFrequentlyUsed(String name, T data) {
        topKRepository.addItem(KEY, name);

        long count = topKRepository.getCount(KEY, name);
        if (count >= THRESHOLD) {
            jedis.set(CACHE_KEY + name, gson.toJson(data));
            log.info("Request is frequently used, add in Jedis");
        } else {
            log.info("Request is not frequently used, skipping");
        }
    }
}