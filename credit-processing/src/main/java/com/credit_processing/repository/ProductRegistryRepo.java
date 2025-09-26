package com.credit_processing.repository;


import com.credit_processing.entity.ProductRegistry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface ProductRegistryRepo extends JpaRepository<ProductRegistry, Long> {
    List<ProductRegistry> findByClientId(String clientId);
    @Query("select coalesce(sum(p.principalAmount), 0) from ProductRegistry p where p.clientId = :clientId")
    BigDecimal sumPrincipalByClientId(@Param("clientId") String clientId);
}
