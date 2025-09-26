package com.account_processing.kafka;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ClientTransactionListener {
    @KafkaListener(
            topics = "${topics.client-transactions}",
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "clientTransactionKafkaListenerContainerFactory"
    )
    public void onMessage(@Payload Object raw) {
        log.info("client_transactions <- {}", raw);
        // TODO: обработать транзакции
    }

}
