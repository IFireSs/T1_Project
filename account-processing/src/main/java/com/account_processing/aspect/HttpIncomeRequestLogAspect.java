package com.account_processing.aspect;

import com.account_processing.aspect.annotations.HttpIncomeRequestLog;
import com.account_processing.dto.HttpRequestEvent;
import com.account_processing.kafka.LoggingKafkaProducer;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import java.time.Instant;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class HttpIncomeRequestLogAspect {

    private final LoggingKafkaProducer loggingKafkaProducer;

    @Before("@annotation(ann)")
    public void before(JoinPoint jp, HttpIncomeRequestLog ann) {
        MethodSignature sig = (MethodSignature) jp.getSignature();
        String methodSignature = sig.toLongString();

        String uri = null; String query = null; String httpMethod = null;
        try {
            RequestAttributes attrs = RequestContextHolder.getRequestAttributes();
            if (attrs != null) {
                HttpServletRequest req = (HttpServletRequest) attrs.resolveReference(RequestAttributes.REFERENCE_REQUEST);
                if (req != null) {
                    uri = req.getRequestURI();
                    query = req.getQueryString();
                    httpMethod = req.getMethod();
                }
            }
        } catch (Exception ignored) {}


        var event = HttpRequestEvent.builder()
                .timestamp(Instant.now())
                .method(methodSignature)
                .direction("IN")
                .uri(uri)
                .query(query)
                .httpMethod(httpMethod)
                .params(jp.getArgs())
                .body(jp.getArgs())
                .build();

        log.info("HTTP IN <- {}", event);
        try {
            loggingKafkaProducer.send(ann.value(), event);
        } catch (Exception e) {
            log.warn("Failed to send http income log to Kafka: {}", e.getMessage());
        }
    }
}