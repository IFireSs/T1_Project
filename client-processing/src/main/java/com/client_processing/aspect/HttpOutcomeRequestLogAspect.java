package com.client_processing.aspect;

import com.client_processing.aspect.annotations.HttpOutcomeRequestLog;
import com.client_processing.dto.HttpRequestEvent;
import com.client_processing.kafka.LoggingKafkaProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class HttpOutcomeRequestLogAspect {

    private final LoggingKafkaProducer loggingKafkaProducer;

    @AfterReturning(pointcut = "@annotation(ann)", returning = "retVal")
    public void after(JoinPoint jp, HttpOutcomeRequestLog ann, Object retVal) {
        MethodSignature sig = (MethodSignature) jp.getSignature();
        String methodSignature = sig.toLongString();

        var event = HttpRequestEvent.builder()
                .timestamp(Instant.now())
                .method(methodSignature)
                .direction("OUT")
                .params(jp.getArgs())
                .result(retVal)
                .build();

        log.info("HTTP OUT -> {}", event);
        try {
            loggingKafkaProducer.send(ann.value(), event);
        } catch (Exception e) {
            log.warn("Failed to send http outcome log to Kafka: {}", e.getMessage());
        }
    }
}