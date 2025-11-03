package com.account_processing.kafka;

import com.account_processing.dto.ClientTransactionMessage;
import com.account_processing.service.TransactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ClientTransactionListener {
    private final TransactionService transactionService;

    @KafkaListener(
            topics = "${topics.client-transactions}",
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "clientTransactionKafkaListenerContainerFactory"
    )
    public void onMessage(@Payload ClientTransactionMessage msg) {
        try {
            log.info("client_transactions <- {}", msg);
            transactionService.handle(msg);
        } catch (Exception e) {
            log.error("Failed to process client_transactions message: {}", msg, e);
            throw e;
        }
    }

}
