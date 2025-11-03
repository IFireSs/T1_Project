package com.account_processing.service;


import com.account_processing.dto.ClientPaymentMessage;
import com.account_processing.entity.Account;
import com.account_processing.entity.Payment;
import com.account_processing.enums.PaymentType;
import com.account_processing.repository.AccountRepository;
import com.account_processing.repository.PaymentRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;


@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {
    private final AccountRepository accountRepo;
    private final PaymentRepository paymentRepo;

    @Transactional
    public void handle(ClientPaymentMessage msg) {
        if (msg == null) return;
        Optional<Account> accOpt = accountRepo.findByProductId(msg.getProductId());
        if (accOpt.isEmpty()) {
            log.warn("Account by productId={} not found, skip payment {}", msg.getProductId(), msg.getId());
            return;
        }
        Account acc = accOpt.get();
        BigDecimal debt = acc.getBalance().compareTo(BigDecimal.ZERO) < 0
                ? acc.getBalance().abs()
                : BigDecimal.ZERO;


        if (msg.getAmount() == null) return;
        BigDecimal amount = msg.getAmount().abs();
        if (amount.compareTo(debt) != 0) {
            log.info("Client payment {} ignored: amount {} != debt {}", msg.getId(), amount, debt);
            return;
        }

        acc.setBalance(acc.getBalance().add(amount));
        accountRepo.save(acc);
        Payment p = new Payment();
        p.setAccount(acc);
        p.setPaymentDate(LocalDate.now());
        p.setAmount(amount);
        p.setIsCredit(Boolean.TRUE);
        p.setPayedAt(Instant.now());
        p.setType(PaymentType.CASH_IN);
        p.setExpired(Boolean.FALSE);
        paymentRepo.save(p);

        List<Payment> open = paymentRepo.findByAccountAndPayedAtIsNullOrderByPaymentDateAsc(acc);
        for (Payment it : open) {
            it.setPayedAt(Instant.now());
            paymentRepo.save(it);
        }
        log.info("Client payment applied. productId={}, amount={}, debt closed.", acc.getProductId(), amount);
    }
}