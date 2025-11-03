package com.account_processing.kafka;


import com.account_processing.dto.ClientPaymentMessage;
import com.account_processing.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;


@Slf4j
@Component
@RequiredArgsConstructor
public class ClientPaymentListener {
    private final PaymentService paymentService;

    @KafkaListener(
            topics = "${topics.client-payments}",
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "clientPaymentKafkaListenerContainerFactory"
    )
    public void onMessage(@Payload ClientPaymentMessage msg) {
        try {
            log.info("client_payments <- {}", msg);
            paymentService.handle(msg);
        } catch (Exception e) {
            log.error("Failed to process client_payments message: {}", msg, e);
            throw e;
        }
    }
}