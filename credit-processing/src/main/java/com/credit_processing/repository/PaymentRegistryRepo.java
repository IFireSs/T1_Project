package com.credit_processing.repository;

import com.credit_processing.entity.PaymentRegistry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRegistryRepo extends JpaRepository<PaymentRegistry, Long> {
    boolean existsByProductRegistryId_ClientIdAndExpiredTrue(String clientId);
}
