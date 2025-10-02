package com.account_processing.service;

import com.account_processing.aspect.annotations.LogDatasourceError;
import com.account_processing.dto.CreateCardRequest;
import com.account_processing.entity.Account;
import com.account_processing.entity.Card;
import com.account_processing.repository.AccountRepository;
import com.account_processing.repository.CardRepository;
import com.account_processing.enums.PaymentSystem;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class CardService {
    private final AccountRepository accountRepository;
    private final CardRepository cardRepository;

    private static final SecureRandom RND = new SecureRandom();

    @LogDatasourceError
    @Transactional
    public void createIfAccountNotBlocked(CreateCardRequest req) {
        var accOpt = accountRepository.findByProductId(req.getProductId());
        if (accOpt.isEmpty()) {
            log.warn("Skip card: productId {} not found (clientId from event={})",
                    req.getProductId(), req.getClientId());
            return;
        }

        var acc = accOpt.get();

        if (!Objects.equals(acc.getClientId(), req.getClientId())) {
            log.warn("Skip card: productId {} belongs to clientId={}, but event has clientId={}",
                    req.getProductId(), acc.getClientId(), req.getClientId());
            return;
        }

        if (isBlocked(acc)) {
            log.info("Skip card: account {} is BLOCKED", req.getProductId());
            return;
        }

        String pan = generateUniquePan(req.getPaymentSystem(), 5);
        Card card = new Card();
        card.setAccount(acc);
        card.setPaymentSystem(req.getPaymentSystem());
        card.setCardId(pan);
        try {
            cardRepository.save(card);
        } catch (DataIntegrityViolationException e) {
            pan = generateUniquePan(req.getPaymentSystem(), 5);
            card.setCardId(pan);
            cardRepository.save(card);
        }
        acc.setCardExist(true);
        accountRepository.save(acc);
    }

    private boolean isBlocked(Account acc) {
        return acc.getStatus() != null && acc.getStatus().name().equalsIgnoreCase("BLOCKED");
    }
    private String generateUniquePan(PaymentSystem ps, int maxAttempts) {
        for (int i = 0; i < maxAttempts; i++) {
            String pan = generatePan(ps);
            if (!cardRepository.existsByCardId(pan)) return pan;
        }
        return generatePan(ps);
    }

    private String generatePan(PaymentSystem ps) {
        String bin;
        switch (ps) {
            case VISA -> bin = "400000";
            case MASTERCARD -> bin = "510000";
            case MIR -> bin = "220000";
            default -> bin = "999999";
        }
        int totalLen = 16;
        int bodyLen = totalLen - 1;
        StringBuilder sb = new StringBuilder(bin);
        while (sb.length() < bodyLen) {
            sb.append(RND.nextInt(10));
        }
        int check = luhnCheckDigit(sb.toString());
        sb.append(check);
        return sb.toString();
    }

    private int luhnCheckDigit(String digits) {
        int sum = 0;
        boolean dbl = true;
        for (int i = digits.length() - 1; i >= 0; i--) {
            int d = digits.charAt(i) - '0';
            if (dbl) {
                d *= 2;
                if (d > 9) d -= 9;
            }
            sum += d;
            dbl = !dbl;
        }
        return (10 - (sum % 10)) % 10;
    }
}
