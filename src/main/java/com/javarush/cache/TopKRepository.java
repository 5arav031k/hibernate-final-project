package com.javarush.cache;

import com.javarush.config.RedisConfig;
import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.UnifiedJedis;
import redis.clients.jedis.util.SafeEncoder;

@Slf4j
public class TopKRepository {
    private final UnifiedJedis jedis = RedisConfig.getUnifiedJedis();

    public void initializeTopK(String key, int k) {
        if (!jedis.exists(key)) {
            jedis.topkReserve(key, k);
        } else {
            log.info("TopK key already exists, skipping TOPK.RESERVE");
        }
    }

    public void addItem(String key, String item) {
        jedis.topkAdd(key, item);
    }

    public long getCount(String key, String item) {
        String count = jedis.sendCommand(() -> SafeEncoder.encode("TOPK.COUNT"), SafeEncoder.encodeMany(key, String.valueOf(item))).toString();
        return Long.parseLong(count.replaceAll("[\\[\\]]", ""));
    }
}
