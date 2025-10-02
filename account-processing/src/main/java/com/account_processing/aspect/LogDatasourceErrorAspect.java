package com.account_processing.aspect;

import com.account_processing.aspect.annotations.LogDatasourceError;
import com.account_processing.dto.DatasourceErrorEvent;
import com.account_processing.entity.ErrorLog;
import com.account_processing.kafka.LoggingKafkaProducer;
import com.account_processing.repository.ErrorLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.Instant;
import java.util.Arrays;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class LogDatasourceErrorAspect {

    private final LoggingKafkaProducer loggingKafkaProducer;
    private final ErrorLogRepository errorLogRepository;

    @Value("${app.name:account-processing}")
    private String appName;

    @Around("@annotation(ann)")
    public Object around(ProceedingJoinPoint pjp, LogDatasourceError ann) throws Throwable {
        try {
            return pjp.proceed();
        } catch (Throwable ex) {
            String type = ann.value();
            MethodSignature sig = (MethodSignature) pjp.getSignature();
            String methodSignature = sig.toLongString();

            var event = DatasourceErrorEvent.builder()
                    .timestamp(Instant.now())
                    .method(methodSignature)
                    .exception(ex.getClass().getName() + ": " + ex.getMessage())
                    .stacktrace(stackToString(ex))
                    .params(pjp.getArgs())
                    .build();

            log.error("Datasource error in {}: {}", methodSignature, ex.getMessage(), ex);

            boolean kafkaOk = true;
            try {
                loggingKafkaProducer.send(type, event);
            } catch (Exception e) {
                kafkaOk = false;
            }
            if (!kafkaOk) {
                try {
                    errorLogRepository.save(ErrorLog.builder()
                            .timestamp(event.getTimestamp())
                            .methodSignature(event.getMethod())
                            .exceptionMessage(ex.getMessage())
                            .stacktrace(event.getStacktrace())
                            .paramsJson(Arrays.deepToString(pjp.getArgs()))
                            .serviceName(appName)
                            .type(type)
                            .build());
                } catch (Exception dbEx) {
                    log.error("Failed to store error log to DB: {}", dbEx.getMessage(), dbEx);
                }
            }
            throw ex;
        }
    }

    private String stackToString(Throwable t) {
        StringWriter sw = new StringWriter();
        t.printStackTrace(new PrintWriter(sw));
        return sw.toString();
    }
}