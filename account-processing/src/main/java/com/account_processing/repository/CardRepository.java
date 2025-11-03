package com.account_processing.repository;

import com.account_processing.entity.Account;
import com.account_processing.entity.Card;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CardRepository extends JpaRepository<Card, Long> {
    List<Card> findAllByAccount(Account accountId);
    boolean existsByCardId(String cardId);
    Optional<Card> findByCardId(String cardId);
}
