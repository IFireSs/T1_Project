package com.credit_processing.kafka;

import com.credit_processing.dto.ClientProductMessage;
import com.credit_processing.service.CreditDecisionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ClientCreditProductListener {
    private final CreditDecisionService decisionService;

    @KafkaListener(
            topics = "${topics.client-credit-products}",
            containerFactory = "clientCreditKafkaListenerContainerFactory"
    )
    public void onCreditProduct(@Payload ClientProductMessage msg) {
        log.info("client_credit_products <- {}", msg);
        try {
            decisionService.handle(msg);
        } catch (Exception e) {
            log.error("Failed to process credit message {}", msg, e);
            throw e;
        }
    }
}
