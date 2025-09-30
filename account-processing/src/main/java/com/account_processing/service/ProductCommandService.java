package com.account_processing.service;

import com.account_processing.dto.ClientProductMessage;
import com.account_processing.entity.Account;
import com.account_processing.enums.AccountStatus;
import com.account_processing.repository.AccountRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductCommandService {
    private final AccountRepository productRepository;

    @Transactional
    public void handle(ClientProductMessage msg) {
        switch (msg.getOp()) {
            case CREATE -> create(msg.getClientId(), msg.getProductId(), msg.getOpenDate(), msg.getCloseDate());
            case UPDATE -> update(msg.getClientId(), msg.getProductId(), msg.getOpenDate(), msg.getCloseDate());
            case DELETE -> delete(msg.getClientId(), msg.getProductId());
        }
    }

    @Transactional
    public void create(String clientId, String productId, LocalDate open, LocalDate close) {
        var existing = productRepository.findByProductId(productId);
        if (existing.isPresent()) {
            if (!Objects.equals(existing.get().getClientId(), clientId)) {
                log.warn("ProductId {} уже привязан к другому clientId={} (получили clientId={})",
                        productId, existing.get().getClientId(), clientId);
            }
            return;
        }
        Account acc = new Account();
        acc.setClientId(clientId);
        acc.setProductId(productId);
        acc.setBalance(BigDecimal.ZERO);
        acc.setInterestRate(BigDecimal.ZERO);
        acc.setCardExist(false);
        acc.setIsRecalc(false);
        acc.setStatus(AccountStatus.ACTIVE);
        try {
            productRepository.save(acc);
        } catch (org.springframework.dao.DataIntegrityViolationException e) {
            log.info("Account create race for productId={}, treating as idempotent", productId);
        }
    }

    @Transactional
    public void update(String clientId, String productId, LocalDate open, LocalDate close) {
        productRepository.findByProductId(productId).ifPresentOrElse(acc -> {
            if (!Objects.equals(acc.getClientId(), clientId)) {
                log.warn("Update: productId={} belongs to clientId={} but request clientId={}",
                        productId, acc.getClientId(), clientId);
            }
            if (close != null) {
                acc.setStatus(AccountStatus.CLOSED);
            }
            productRepository.save(acc);
        }, () -> log.warn("Account for productId={} not found on update", productId));
    }

    @Transactional
    public void delete(String clientId, String productId) {
        productRepository.findByProductId(productId).ifPresent(acc -> {
            if (!Objects.equals(acc.getClientId(), clientId)) {
                log.warn("Запрос на удаление: productId={} у clientId={}, но в БД clientId={}",
                        productId, clientId, acc.getClientId());
            }
            acc.setStatus(AccountStatus.CLOSED);
            productRepository.save(acc);
        });
    }



}
