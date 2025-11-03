package com.account_processing.kafka;

import com.account_processing.dto.CreateCardRequest;
import com.account_processing.service.CardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ClientCardListener {
    private final CardService cardCommandService;

    @KafkaListener(
            topics = "${topics.client-cards}",
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "clientCardKafkaListenerContainerFactory"
    )
    public void onMessage(@Payload CreateCardRequest req) {
        try {
            log.info("client_cards <- {}", req);
            cardCommandService.createIfAccountNotBlocked(req);
        } catch (Exception e) {
            log.error("Failed to process client_cards message: {}", req, e);
            throw e;
        }
    }
}
