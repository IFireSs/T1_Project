package com.account_processing.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Slf4j
@Component
@RequiredArgsConstructor
public class LoggingKafkaProducer {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${app.name:account-processing}")
    private String appName;

    @Value("${topics.service-logs:service_logs}")
    private String serviceLogsTopic;

    public void send(String typeHeader, Object payload) {
        try {
            var record = new ProducerRecord<String, Object>(serviceLogsTopic, appName, payload);
            record.headers().add(new RecordHeader("type", typeHeader.getBytes(StandardCharsets.UTF_8)));
            kafkaTemplate.send(record).whenComplete((res, ex) -> {
                if (ex != null) {
                    log.error("Failed to publish service log to Kafka, falling back to DB. {}", ex.getMessage());
                } else {
                    log.debug("Service log sent to Kafka topic={} partition={} offset={}",
                            res.getRecordMetadata().topic(), res.getRecordMetadata().partition(), res.getRecordMetadata().offset());
                }
            });
        } catch (Exception e) {
            log.error("Kafka publish failed: {}", e.getMessage(), e);
            throw e;
        }
    }
}