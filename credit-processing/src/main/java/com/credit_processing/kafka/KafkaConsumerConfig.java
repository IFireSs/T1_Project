package com.credit_processing.kafka;


import com.credit_processing.dto.ClientProductMessage;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.*;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.util.backoff.ExponentialBackOff;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@EnableKafka
@Configuration
public class KafkaConsumerConfig {

    @Bean
    public DefaultErrorHandler kafkaErrorHandler() {
        var backoff = new ExponentialBackOff(1_000, 2.0);
        backoff.setMaxInterval(30_000);
        return new DefaultErrorHandler(backoff);
    }

    private Map<String,Object> baseConsumerProps(KafkaProperties props) {
        return new HashMap<>(props.buildConsumerProperties(null));
    }

    @Bean
    public ConsumerFactory<String, ClientProductMessage> clientCreditConsumerFactory(KafkaProperties props) {
        Map<String,Object> cfg = baseConsumerProps(props);
        JsonDeserializer<ClientProductMessage> val = new JsonDeserializer<>(ClientProductMessage.class, false);
        val.addTrustedPackages("*");
        val.ignoreTypeHeaders();
        return new DefaultKafkaConsumerFactory<>(cfg, new StringDeserializer(), val);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, ClientProductMessage>
    clientCreditKafkaListenerContainerFactory(
            ConsumerFactory<String, ClientProductMessage> f,
            DefaultErrorHandler eh
    ) {
        var factory = new ConcurrentKafkaListenerContainerFactory<String, ClientProductMessage>();
        factory.setConsumerFactory(f);
        factory.setCommonErrorHandler(eh);
        return factory;
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
