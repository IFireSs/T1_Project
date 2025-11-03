package com.client_processing.aspect;

import com.client_processing.aspect.annotations.Metric;
import com.client_processing.dto.SlowMethodEvent;
import com.client_processing.kafka.LoggingKafkaProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.concurrent.TimeUnit;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class MetricAspect {

    private final LoggingKafkaProducer kafka;

    @Value("${app.metric.threshold-ms:250}")
    private long globalThresholdMs;

    @Around("@annotation(metric)")
    public Object measure(ProceedingJoinPoint pjp, Metric metric) throws Throwable {
        long startedNs = System.nanoTime();
        try {
            return pjp.proceed();
        } finally {
            long tookMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startedNs);
            long threshold = metric.thresholdMs() > 0 ? metric.thresholdMs() : globalThresholdMs;

            MethodSignature sig = (MethodSignature) pjp.getSignature();
            String methodSig = sig.getDeclaringType().getSimpleName() + "#" + sig.getName();

            if (tookMs > threshold) {
                var event = SlowMethodEvent.builder()
                        .timestamp(Instant.now())
                        .method(metric.value().isBlank() ? methodSig : metric.value())
                        .durationMs(tookMs)
                        .params(metric.includeParams() ? pjp.getArgs() : null)
                        .build();
                try {
                    kafka.send("WARNING", event);
                } catch (Exception e) {
                    log.warn("Failed to send WARNING metric to Kafka: {}", e.getMessage());
                }
            }
        }
    }
}
