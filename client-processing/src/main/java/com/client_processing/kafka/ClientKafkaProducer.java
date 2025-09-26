package com.client_processing.kafka;

import com.client_processing.dto.kafka.ClientProductMessage;
import com.client_processing.dto.kafka.CreateCardRequest;
import com.client_processing.enums.ProductKey;
import com.client_processing.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.client_processing.enums.ProductKey;


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

    private final ProductRepository productRepository;
    private final KafkaTemplate<String, Object> template;

    public void publishClientProduct(ClientProductMessage msg) {
        String topic = isCredit(productRepository.findByProductId(msg.getProductId()).getKey()) ? topicClientCreditProducts: topicClientProducts;
        template.send(topic, String.valueOf(msg.getClientId()), msg);
    }

    public void publishCardCreateRequest(CreateCardRequest req) {
        template.send(topicClientCards, String.valueOf(req.getClientId()), req);
    }

    private boolean isCredit(ProductKey k) {
        return k == ProductKey.IPO || k == ProductKey.PC || k == ProductKey.AC;
    }
}