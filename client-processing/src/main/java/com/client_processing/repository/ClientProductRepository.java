package com.client_processing.repository;

import com.client_processing.entity.ClientProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClientProductRepository extends JpaRepository<ClientProduct, Long> {
    List<ClientProduct> findByClientId(String clientId);
    Optional<ClientProduct> findByProductId(String productId);
    List<ClientProduct> findAllByProductId(String productId);
    void deleteByProductId(String productId);
    boolean existsByProductId(String productId);
}