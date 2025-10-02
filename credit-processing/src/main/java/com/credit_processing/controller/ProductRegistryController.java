package com.credit_processing.controller;

import com.credit_processing.aspect.annotations.HttpIncomeRequestLog;
import com.credit_processing.entity.ProductRegistry;
import com.credit_processing.repository.ProductRegistryRepo;
import com.credit_processing.service.ProductRegistryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/cred")
@RequiredArgsConstructor
public class ProductRegistryController {
    private final ProductRegistryRepo productRegistryRepo;
    private final ProductRegistryService productRegistryService;

    @GetMapping
    @HttpIncomeRequestLog
    public ResponseEntity<List<ProductRegistry>> getAccounts() {
        return new ResponseEntity<>(productRegistryService.findProductRegistry(), HttpStatus.OK);
    }
}
