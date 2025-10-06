package com.client_processing.aspect;

import com.client_processing.aspect.annotations.Cached;
import com.client_processing.cache.TtlCacheManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class CachedAspect {

    private final TtlCacheManager cacheManager;

    @Value("${app.cache.ttl-ms:60000}")
    private long globalTtlMs;

    @Around("@annotation(cfg)")
    public Object around(ProceedingJoinPoint pjp, Cached cfg) throws Throwable {
        MethodSignature sig = (MethodSignature) pjp.getSignature();

        String cacheName = cfg.cache();
        if (cacheName == null || cacheName.isBlank()) {
            Class<?> rt = sig.getReturnType();
            cacheName = (rt != null && rt != Void.TYPE) ? rt.getSimpleName() : sig.getName();
        }

        Object key = buildKey(pjp.getArgs());

        long ttlMs = cfg.ttlMs() > 0 ? cfg.ttlMs() : globalTtlMs;

        var cached = cacheManager.get(cacheName, key);
        if (cached.isPresent()) {
            return cached.get();
        }

        Object value = pjp.proceed();

        if (value != null || cfg.cacheNull()) {
            cacheManager.put(cacheName, key, value, ttlMs);
        }
        return value;
    }

    private Object buildKey(Object[] args) {
        if (args == null || args.length == 0) {
            return 0;
        }
        if (args.length == 1 && isSimpleKey(args[0])) {
            return args[0];
        }
        int h = 1;
        for (Object a : args) {
            h = 31 * h + (a == null ? 0 : a.hashCode());
        }
        return h;
    }

    private boolean isSimpleKey(Object o) {
        if (o == null) return false;
        Class<?> c = o.getClass();
        return CharSequence.class.isAssignableFrom(c)
                || Number.class.isAssignableFrom(c)
                || c == Long.TYPE || c == Integer.TYPE || c == Short.TYPE || c == Byte.TYPE
                || c == java.util.UUID.class;
    }
}
