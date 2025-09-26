package com.account_processing.kafka;

import com.account_processing.dto.ClientProductMessage;
import com.account_processing.service.ProductCommandService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ClientProductListener {
    private final ProductCommandService productCommandService;

    @KafkaListener(
            topics = "${topics.client-products}",
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "clientProductKafkaListenerContainerFactory"
    )
    public void onMessage(@Payload ClientProductMessage msg) {
        try {
            log.info("client_products <- {}", msg);
            productCommandService.handle(msg);
        } catch (Exception e) {
            log.error("Failed to process client_products message: {}", msg, e);
            throw e;
        }
    }
}
