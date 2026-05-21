package com.camtech.digitranscm.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;

@Service
public class CacheService {

    private static final Logger log = LoggerFactory.getLogger(CacheService.class);

    private final RedisTemplate<String, Object> redisTemplate;
    private final Queue<PendingSync> syncQueue = new ConcurrentLinkedQueue<>();
    private final Map<String, Object> localCache = new ConcurrentHashMap<>();

    private static final long DEFAULT_TTL = 30;
    private volatile boolean online = true;

    public CacheService(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public boolean isOnline() {
        try {
            redisTemplate.getConnectionFactory().getConnection().ping();
            online = true;
        } catch (Exception e) {
            online = false;
        }
        return online;
    }

    public void put(String key, Object value) {
        if (isOnline()) {
            try {
                redisTemplate.opsForValue().set(key, value, DEFAULT_TTL, TimeUnit.MINUTES);
                log.debug("Cached in Redis: {}", key);
            } catch (Exception e) {
                log.warn("Redis unavailable, falling back to local cache: {}", key);
                localCache.put(key, value);
                syncQueue.add(new PendingSync(key, value, PendingSync.Operation.SET));
            }
        } else {
            localCache.put(key, value);
            syncQueue.add(new PendingSync(key, value, PendingSync.Operation.SET));
            log.info("Offline: queued SET for {}", key);
        }
    }

    public void put(String key, Object value, long ttlMinutes) {
        if (isOnline()) {
            try {
                redisTemplate.opsForValue().set(key, value, ttlMinutes, TimeUnit.MINUTES);
            } catch (Exception e) {
                localCache.put(key, value);
                syncQueue.add(new PendingSync(key, value, PendingSync.Operation.SET));
            }
        } else {
            localCache.put(key, value);
            syncQueue.add(new PendingSync(key, value, PendingSync.Operation.SET));
        }
    }

    public Object get(String key) {
        if (isOnline()) {
            try {
                Object val = redisTemplate.opsForValue().get(key);
                if (val != null) return val;
            } catch (Exception e) {
                log.warn("Redis unavailable for GET, trying local cache");
            }
        }
        return localCache.get(key);
    }

    public void evict(String key) {
        if (isOnline()) {
            try {
                redisTemplate.delete(key);
            } catch (Exception e) {
                syncQueue.add(new PendingSync(key, null, PendingSync.Operation.DELETE));
            }
        } else {
            syncQueue.add(new PendingSync(key, null, PendingSync.Operation.DELETE));
        }
        localCache.remove(key);
    }

    public boolean hasKey(String key) {
        if (isOnline()) {
            try {
                return Boolean.TRUE.equals(redisTemplate.hasKey(key));
            } catch (Exception e) {
                return localCache.containsKey(key);
            }
        }
        return localCache.containsKey(key);
    }

    public void syncPending() {
        if (!isOnline()) {
            log.info("Still offline, cannot sync");
            return;
        }
        int count = 0;
        PendingSync task;
        while ((task = syncQueue.poll()) != null) {
            try {
                switch (task.operation()) {
                    case SET -> redisTemplate.opsForValue().set(task.key(), task.value(), DEFAULT_TTL, TimeUnit.MINUTES);
                    case DELETE -> redisTemplate.delete(task.key());
                }
                count++;
            } catch (Exception e) {
                log.error("Sync failed for key: {}", task.key(), e);
                syncQueue.add(task);
                break;
            }
        }
        if (count > 0) log.info("Synced {} pending operations to Redis", count);
    }
}
