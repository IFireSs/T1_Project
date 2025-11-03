package com.client_processing.controller;


import com.client_processing.dto.Dto;
import com.client_processing.dto.ProductDto;
import com.client_processing.service.ProductService;
import com.ms.aspects.annotations.HttpIncomeRequestLog;
import com.ms.aspects.annotations.Metric;
import jakarta.websocket.server.PathParam;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService service;

    @Metric
    @HttpIncomeRequestLog
    @PreAuthorize("hasRole('MASTER')")
    @PostMapping
    public ResponseEntity<Dto> create(@RequestBody ProductDto dto) {
        return service.create(dto);
    }

    @Metric
    @HttpIncomeRequestLog
    @PreAuthorize("hasAnyRole('MASTER','GRAND_EMPLOYEE')")
    @PutMapping("/{productId}")
    public ResponseEntity<Dto> update(@PathVariable String productId, @RequestBody ProductDto dto) {
        dto.setProductId(productId);
        return service.update(dto);
    }

    @Metric
    @HttpIncomeRequestLog
    @GetMapping("/{productId}")
    public ResponseEntity<Dto> get(@PathVariable("productId") String id) {
        return service.get(id);
    }

    @Metric
    @HttpIncomeRequestLog
    @GetMapping
    public ResponseEntity<List<ProductDto>> list() {
        return service.list();
    }

    @Metric
    @HttpIncomeRequestLog
    @PreAuthorize("hasAnyRole('MASTER','GRAND_EMPLOYEE')")
    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> delete(@PathVariable String productId) {
        service.delete(productId);
        return ResponseEntity.noContent().build();
    }
}
