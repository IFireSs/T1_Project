package com.account_processing.repository;

import com.account_processing.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    Optional<Account> findByProductId(String productId);
    Optional<Account> findTopByClientIdAndProductIdOrderByIdDesc(String clientId, String productId);
    List<Account> findAllByClientIdAndProductId(String clientId, String productId);
    List<Account> findAllByClientId(String clientId);

}
