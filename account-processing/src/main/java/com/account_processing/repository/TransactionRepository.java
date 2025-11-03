package com.account_processing.repository;

import com.account_processing.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    long countByCardId_CardIdAndDateAfter(String cardId, Instant fromTs);
}