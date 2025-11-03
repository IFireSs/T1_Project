package com.client_processing.service;

import com.client_processing.aspect.annotations.Cached;
import com.client_processing.aspect.annotations.LogDatasourceError;
import com.client_processing.dto.CreateCardDto;
import com.client_processing.kafka.ClientKafkaProducer;
import com.client_processing.mapper.KafkaMapper;
import com.client_processing.repository.ClientRepository;
import com.client_processing.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service @RequiredArgsConstructor
public class CardService {
    private final ClientKafkaProducer producer;
    private final KafkaMapper kafkaMapper;
    private final ClientRepository clientRepository;
    private final ProductRepository productRepository;

    @Cached
    @LogDatasourceError
    public ResponseEntity<Void> requestCard(CreateCardDto dto) {
        if(!clientRepository.existsByClientId(dto.getClientId())) {
            return ResponseEntity.notFound().build();
        }
        if(!productRepository.existsByProductId(dto.getProductId())) {
            return ResponseEntity.notFound().build();
        }
        var req = kafkaMapper.toCreateCardRequest(dto);
        producer.publishCardCreateRequest(req);
        return ResponseEntity.accepted().build();
    }
}