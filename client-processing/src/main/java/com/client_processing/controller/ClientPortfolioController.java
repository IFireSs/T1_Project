package com.client_processing.controller;

import com.client_processing.aspect.annotations.HttpIncomeRequestLog;
import com.client_processing.dto.ClientProductDto;
import com.client_processing.dto.Dto;
import com.client_processing.enums.ProductKey;
import com.client_processing.service.ClientPortfolioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/client-products")
@RequiredArgsConstructor
public class ClientPortfolioController {

    private final ClientPortfolioService service;

    @HttpIncomeRequestLog
    @PostMapping
    public ResponseEntity<Dto> create(@RequestBody ClientProductDto dto) {
        return service.create(dto);
    }

    @HttpIncomeRequestLog
    @PutMapping("/{productId}")
    public ResponseEntity<Dto> update(@PathVariable String productId, @RequestBody ClientProductDto dto) {
        dto.setProductId(productId);
        return service.update(dto);
    }

    @HttpIncomeRequestLog
    @GetMapping("/{productId}")
    public ResponseEntity<Dto> get(@PathVariable String productId) {
        return service.get(productId);
    }

    @HttpIncomeRequestLog
    @GetMapping
    public ResponseEntity<List<ClientProductDto>> list() {
        return service.list();
    }

    @HttpIncomeRequestLog
    @DeleteMapping
    public ResponseEntity<Void> delete(@RequestBody ClientProductDto dto) {
        service.delete(dto);
        return ResponseEntity.noContent().build();
    }
}