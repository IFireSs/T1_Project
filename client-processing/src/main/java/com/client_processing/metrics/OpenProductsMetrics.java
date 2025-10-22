package com.client_processing.metrics;

import com.client_processing.repository.OpenProductsMetricsRepository;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.binder.MeterBinder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
@Component
@RequiredArgsConstructor
public class OpenProductsMetrics implements MeterBinder {

    private final OpenProductsMetricsRepository repo;
    private final Map<String, AtomicLong> values = new ConcurrentHashMap<>();
    private final Set<String> registered = new ConcurrentSkipListSet<>();
    private MeterRegistry registry;

    @Override
    public void bindTo(MeterRegistry registry) {
        this.registry = registry;
        ensureGauge("ALL");
    }

    private AtomicLong valueOf(String key) {
        return values.computeIfAbsent(key, k -> new AtomicLong(0));
    }

    private void ensureGauge(String key) {
        if (registry != null && registered.add(key)) {
            Gauge.builder("business_open_products_total", () -> valueOf(key).get())
                    .description("Open client products grouped by product key")
                    .tag("product_key", key)
                    .register(registry);
        }
    }

    @Scheduled(initialDelay = 3000, fixedDelay = 30000)
    public void refresh() {
        try {
            var rows = repo.countOpenByKey();
            long total = 0;
            for (var row : rows) {
                var key = row.getKey();
                var cnt = row.getCnt();
                ensureGauge(key);
                valueOf(key).set(cnt);
                total += cnt;
            }
            valueOf("ALL").set(total);
        } catch (Exception e) {
            log.warn("Failed to refresh open products metrics: {}", e.toString());
        }
    }
}
