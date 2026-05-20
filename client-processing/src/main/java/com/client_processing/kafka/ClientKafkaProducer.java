package com.client_processing.kafka;

import com.client_processing.dto.kafka.ClientProductMessage;
import com.client_processing.dto.kafka.CreateCardRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ClientKafkaProducer {
    @Value("${topics.client-products}")
    private String topicClientProducts;

    @Value("${topics.client-credit-products}")
    private String topicClientCreditProducts;

    @Value("${topics.client-cards}")
    private String topicClientCards;

    private final KafkaTemplate<String, Object> template;

    public void publishClientProduct(ClientProductMessage msg) {
        template.send(topicClientProducts, String.valueOf(msg.getClientId()), msg);
    }

    public void publishClientCreditProduct(ClientProductMessage msg) {
        template.send(topicClientCreditProducts, String.valueOf(msg.getClientId()), msg);
    }

    public void publishCardCreateRequest(CreateCardRequest req) {
        template.send(topicClientCards, String.valueOf(req.getClientId()), req);
    }
}
