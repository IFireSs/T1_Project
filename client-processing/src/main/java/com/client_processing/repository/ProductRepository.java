package com.client_processing.repository;

import com.client_processing.entity.Product;
import com.client_processing.enums.ProductKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByKey(ProductKey key);
    Boolean existsByProductId(String productId);
    Product findByProductId(String productId);
    void deleteByProductId(String productId);
}