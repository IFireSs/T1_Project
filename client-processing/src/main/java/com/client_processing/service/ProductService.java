package com.client_processing.service;

import com.client_processing.dto.Dto;
import com.client_processing.dto.ErrorDto;
import com.client_processing.dto.ProductDto;
import com.client_processing.dto.kafka.ClientProductMessage;
import com.client_processing.entity.Product;
import com.client_processing.kafka.ClientKafkaProducer;
import com.client_processing.mapper.ClientProductMapper;
import com.client_processing.mapper.KafkaMapper;
import com.client_processing.mapper.ProductMapper;
import com.client_processing.repository.ClientProductRepository;
import com.client_processing.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository repo;

    @Qualifier("productMapper")
    private final ProductMapper mapper;

    private final ClientProductRepository clientProductRepository;
    private final ClientProductMapper clientProductMapper;
    private final KafkaMapper kafkaMapper;
    private final ClientKafkaProducer producer;

    @Transactional
    public ResponseEntity<Dto> create(ProductDto dto) {
        if(dto.getName() == null || dto.getName().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ErrorDto.builder().Response("Name failed").build());
        }
        if(dto.getKey() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ErrorDto.builder().Response("Key failed").build());
        }
        var product = new Product();
        product.setName(dto.getName());
        product.setKey(dto.getKey());
        product.setCreateDate(Instant.now());
        repo.saveAndFlush(product);
        return ResponseEntity.ok(mapper.toDto(product));
    }

    @Transactional
    public ResponseEntity<Dto> update(ProductDto dto) {
        var clientProduct = clientProductRepository.findByProductId(dto.getProductId());
        if(clientProduct.isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(ErrorDto.builder().Response("productId " + dto.getProductId() + " уже привязан к clientId=" + clientProduct.get().getClientId() + "\nНевозможно обновить привязанный продукт").build() );
        }
        if(!repo.existsByProductId(dto.getProductId())){
            return create(dto);
        }
        var product = repo.findByProductId(dto.getProductId());
        if (dto.getName() != null) product.setName(dto.getName());
        if (dto.getKey()  != null) product.setKey(dto.getKey());
        if (dto.getCreateDate() != null) product.setCreateDate(dto.getCreateDate());
        product = repo.saveAndFlush(product);
        return ResponseEntity.ok(mapper.toDto(product));
    }

    @Transactional(readOnly = true)
    public ResponseEntity<Dto>  get(String productId) {
        if (!repo.existsByProductId(productId)){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        return ResponseEntity.ok(mapper.toDto(repo.findByProductId(productId)));
    }

    @Transactional(readOnly = true)
    public ResponseEntity<List<ProductDto>> list() {
        return ResponseEntity.ok(mapper.toDtoList(repo.findAll()));
    }

    @Transactional
    public void delete(String productId) {
        var product = repo.findByProductId(productId);
        if (product == null) {
            return;
        }
        var links = clientProductRepository.findAllByProductId(productId);
        for (var link : links) {
            var dto = clientProductMapper.toDto(link);
            var msg = kafkaMapper.toClientProductMessage(dto, ClientProductMessage.Op.DELETE);
            producer.publishClientProduct(msg);
        }
        clientProductRepository.deleteByProductId(productId);
        repo.deleteByProductId(productId);
    }
}