package com.client_processing.service;

import com.ms.aspects.annotations.Cached;
import com.ms.aspects.annotations.LogDatasourceError;
import com.client_processing.dto.ClientProductDto;
import com.client_processing.dto.Dto;
import com.client_processing.dto.ErrorDto;
import com.client_processing.dto.kafka.ClientProductMessage;
import com.client_processing.entity.ClientProduct;
import com.client_processing.enums.ProductKey;
import com.client_processing.kafka.ClientKafkaProducer;
import com.client_processing.mapper.ClientProductMapper;
import com.client_processing.mapper.KafkaMapper;
import com.client_processing.repository.ClientProductRepository;
import com.client_processing.repository.ClientRepository;
import com.client_processing.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;


@Service @RequiredArgsConstructor
public class ClientPortfolioService {

    private final ClientKafkaProducer producer;
    private final KafkaMapper kafkaMapper;
    private final ClientProductRepository repo;
    private final ProductRepository productRepo;
    private final ClientRepository clientRepo;

    @Qualifier("clientProductMapper")
    private final ClientProductMapper mapper;

    @Cached
    @LogDatasourceError
    @Transactional
    public ResponseEntity<Dto> create(ClientProductDto dto) {
        if(!clientRepo.existsByClientId(dto.getClientId())) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ErrorDto.builder().Response("Client not found").build());
        }
        var product = productRepo.findByProductId(dto.getProductId());
        if (product == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ErrorDto.builder().Response("Product not found").build());
        }
        var boundOpt = repo.findByProductId(dto.getProductId());
        if (boundOpt.isPresent() && !boundOpt.get().getClientId().equals(dto.getClientId())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(ErrorDto.builder().Response(
                            "productId " + dto.getProductId() + " уже привязан к clientId=" + boundOpt.get().getClientId()
                    ).build());
        }
        var clientProduct = boundOpt.orElseGet(ClientProduct::new);
        clientProduct.setClientId(dto.getClientId());
        clientProduct.setProductId(dto.getProductId());
        clientProduct.setOpenDate(dto.getOpenDate());
        clientProduct.setCloseDate(dto.getCloseDate());
        clientProduct.setStatus(dto.getStatus());
        repo.save(clientProduct);
        var msg = kafkaMapper.toClientProductMessage(dto, ClientProductMessage.Op.CREATE);
        publishClientProduct(product.getKey(), msg);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Cached
    @LogDatasourceError
    @Transactional
    public ResponseEntity<Dto> update(ClientProductDto dto) {
        if (!clientRepo.existsByClientId(dto.getClientId())) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ErrorDto.builder().Response("Client not found").build());
        }
        var product = productRepo.findByProductId(dto.getProductId());
        if (product == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ErrorDto.builder().Response("Product not found").build());
        }


        var boundOpt = repo.findByProductId(dto.getProductId());
        if (boundOpt.isPresent() && !Objects.equals(boundOpt.get().getClientId(), dto.getClientId())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(ErrorDto.builder().Response("productId " + dto.getProductId() + " уже привязан к clientId=" + boundOpt.get().getClientId()).build());
        }

        var clientProduct = boundOpt.orElseGet(ClientProduct::new);
        clientProduct.setClientId(dto.getClientId());
        clientProduct.setProductId(dto.getProductId());
        clientProduct.setOpenDate(dto.getOpenDate());
        clientProduct.setCloseDate(dto.getCloseDate());
        clientProduct.setStatus(dto.getStatus());
        repo.save(clientProduct);

        var msg = kafkaMapper.toClientProductMessage(dto, ClientProductMessage.Op.UPDATE);
        publishClientProduct(product.getKey(), msg);
        return ResponseEntity.ok(dto);

    }

    @Cached
    @LogDatasourceError
    @Transactional(readOnly = true)
    public ResponseEntity<Dto> get(String productId) {
        if(!repo.existsByProductId(productId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ErrorDto.builder().Response("Product not found").build());
        }
        return ResponseEntity.ok(mapper.toDto(repo.findByProductId(productId).orElseThrow()));
    }

    @Cached
    @LogDatasourceError
    @Transactional(readOnly = true)
    public ResponseEntity<List<ClientProductDto>> list() {
        return ResponseEntity.ok(mapper.toDtoList(repo.findAll()));
    }

    @LogDatasourceError
    @Transactional
    public void delete(ClientProductDto dto) {
        var cpOpt = repo.findByProductId(dto.getProductId());
        if (cpOpt.isEmpty()) {
            return;
        }

        var msg = kafkaMapper.toClientProductMessage(dto, ClientProductMessage.Op.DELETE);
        var product = productRepo.findByProductId(dto.getProductId());
        if (product != null) {
            publishClientProduct(product.getKey(), msg);
        }
        repo.deleteByProductId(dto.getProductId());
    }

    private void publishClientProduct(ProductKey key, ClientProductMessage msg) {
        if (isCredit(key)) {
            producer.publishClientCreditProduct(msg);
        } else {
            producer.publishClientProduct(msg);
        }
    }

    private boolean isCredit(ProductKey key) {
        return key == ProductKey.IPO || key == ProductKey.PC || key == ProductKey.AC;
    }
}
