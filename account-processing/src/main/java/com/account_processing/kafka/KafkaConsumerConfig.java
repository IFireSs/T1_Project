package com.account_processing.kafka;

import com.account_processing.dto.ClientProductMessage;
import com.account_processing.dto.ClientTransactionMessage;
import com.account_processing.dto.CreateCardRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.util.backoff.ExponentialBackOff;

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

    @Bean
    public ObjectMapper objectMapper() {
        var m = new com.fasterxml.jackson.databind.ObjectMapper();
        m.registerModule(new JavaTimeModule());
        m.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return m;
    }

    private Map<String, Object> baseConsumerProps(KafkaProperties props) {
        return new HashMap<>(props.buildConsumerProperties(null));
    }


    @Bean
    public ConsumerFactory<String, ClientProductMessage> clientProductConsumerFactory(
            KafkaProperties props
    ) {
        Map<String, Object> cfg = baseConsumerProps(props);

        JsonDeserializer<ClientProductMessage> value = new JsonDeserializer<>(ClientProductMessage.class, false);
        value.addTrustedPackages("*");
        value.ignoreTypeHeaders();

        return new DefaultKafkaConsumerFactory<>(
                cfg,
                new StringDeserializer(),
                value
        );
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, ClientProductMessage>
    clientProductKafkaListenerContainerFactory(
            ConsumerFactory<String, ClientProductMessage> clientProductConsumerFactory,
            DefaultErrorHandler errorHandler
    ) {
        var f = new ConcurrentKafkaListenerContainerFactory<String, ClientProductMessage>();
        f.setConsumerFactory(clientProductConsumerFactory);
        f.setCommonErrorHandler(errorHandler);
        return f;
    }


    @Bean
    public ConsumerFactory<String, CreateCardRequest> clientCardConsumerFactory(
            KafkaProperties props
    ) {
        Map<String, Object> cfg = baseConsumerProps(props);

        JsonDeserializer<CreateCardRequest> value = new JsonDeserializer<>(CreateCardRequest.class, false);
        value.addTrustedPackages("*");
        value.ignoreTypeHeaders();

        return new DefaultKafkaConsumerFactory<>(
                cfg,
                new StringDeserializer(),
                value
        );
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, CreateCardRequest>
    clientCardKafkaListenerContainerFactory(
            ConsumerFactory<String, CreateCardRequest> clientCardConsumerFactory,
            DefaultErrorHandler errorHandler
    ) {
        var f = new ConcurrentKafkaListenerContainerFactory<String, CreateCardRequest>();
        f.setConsumerFactory(clientCardConsumerFactory);
        f.setCommonErrorHandler(errorHandler);
        return f;
    }

    @Bean
    public ConsumerFactory<String, ClientTransactionMessage> clientTransactionConsumerFactory(
            KafkaProperties props
    ) {
        Map<String, Object> cfg = baseConsumerProps(props);

        JsonDeserializer<ClientTransactionMessage> value = new JsonDeserializer<>(ClientTransactionMessage.class, false);
        value.addTrustedPackages("*");
        value.ignoreTypeHeaders();

        return new DefaultKafkaConsumerFactory<>(
                cfg,
                new StringDeserializer(),
                value
        );
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, ClientTransactionMessage>
    clientTransactionKafkaListenerContainerFactory(
            ConsumerFactory<String, ClientTransactionMessage> clientTransactionConsumerFactory,
            DefaultErrorHandler errorHandler
    ) {
        var f = new ConcurrentKafkaListenerContainerFactory<String, ClientTransactionMessage>();
        f.setConsumerFactory(clientTransactionConsumerFactory);
        f.setCommonErrorHandler(errorHandler);
        return f;
    }
    private DefaultErrorHandler errorHandler() {
        var backoff = new ExponentialBackOff(500L, 2.0);
        backoff.setMaxElapsedTime(10_000L);
        return new DefaultErrorHandler(backoff);
    }
}
