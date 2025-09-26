package com.credit_processing.service;

import com.credit_processing.entity.ProductRegistry;
import com.credit_processing.repository.ProductRegistryRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductRegistryService {
    private final ProductRegistryRepo productRegistryRepo;

    public List<ProductRegistry> findProductRegistry() {
        return  productRegistryRepo.findAll();
    }
}
