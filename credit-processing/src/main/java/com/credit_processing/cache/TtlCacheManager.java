package com.credit_processing.cache;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
@RequiredArgsConstructor
public class TtlCacheManager {

    private final Map<String, Map<Object, Entry>> caches = new ConcurrentHashMap<>();

    @Value("${app.cache.ttl-ms:60000}")
    private long defaultTtlMs;

    public Optional<Object> get(String cacheName, Object key) {
        Map<Object, Entry> cache = caches.get(cacheName);
        if (cache == null) return Optional.empty();
        Entry e = cache.get(key);
        if (e == null) return Optional.empty();
        if (e.expiresAt <= System.currentTimeMillis()) {
            cache.remove(key);
            return Optional.empty();
        }
        return Optional.ofNullable(e.value);
    }

    public void put(String cacheName, Object key, Object value, long ttlMs) {
        Map<Object, Entry> cache = caches.computeIfAbsent(cacheName, n -> new ConcurrentHashMap<>());
        long ttl = ttlMs > 0 ? ttlMs : defaultTtlMs;
        cache.put(key, new Entry(value, System.currentTimeMillis() + ttl));
    }

    public void invalidate(String cacheName, Object key) {
        Map<Object, Entry> cache = caches.get(cacheName);
        if (cache != null) cache.remove(key);
    }

    public void invalidateAll(String cacheName) {
        Map<Object, Entry> cache = caches.get(cacheName);
        if (cache != null) cache.clear();
    }

    @Getter
    @ToString
    @AllArgsConstructor
    private static class Entry {
        Object value;
        long expiresAt;
    }
}
