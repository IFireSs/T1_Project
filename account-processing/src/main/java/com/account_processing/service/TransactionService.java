package com.account_processing.service;


import com.account_processing.aspect.annotations.LogDatasourceError;
import com.account_processing.dto.ClientTransactionMessage;
import com.account_processing.entity.Account;
import com.account_processing.entity.Card;
import com.account_processing.entity.Payment;
import com.account_processing.entity.Transaction;
import com.account_processing.enums.AccountStatus;
import com.account_processing.enums.PaymentType;
import com.account_processing.enums.TransactionStatus;
import com.account_processing.enums.TransactionType;
import com.account_processing.repository.AccountRepository;
import com.account_processing.repository.CardRepository;
import com.account_processing.repository.PaymentRepository;
import com.account_processing.repository.TransactionRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionService {
    private final AccountRepository accountRepo;
    private final CardRepository cardRepo;
    private final PaymentRepository paymentRepo;
    private final TransactionRepository txRepo;


    @Value("${processing.fraud.window-seconds:60}")
    private long fraudWindowSeconds;


    @Value("${processing.fraud.threshold:5}")
    private long fraudThreshold;

    @LogDatasourceError
    @Transactional
    public void handle(ClientTransactionMessage msg) {
        if (msg == null) return;
        Optional<Account> accOpt = accountRepo.findByProductId(msg.getProductId());
        if (accOpt.isEmpty()) {
            log.warn("Account by productId={} not found, skip tx {}", msg.getProductId(), msg.getId());
            return;
        }
        Account acc = accOpt.get();

        Card card = null;
        if (msg.getCardId() != null) {
            card = cardRepo.findByCardId(msg.getCardId()).orElse(null);
        }
        Transaction tx = new Transaction();
        tx.setAccount(acc);
        tx.setCardId(card);
        tx.setType(msg.getType());
        tx.setAmount(msg.getAmount());
        tx.setStatus(TransactionStatus.PROCESSING);
        tx.setDate(msg.getTs() != null ? msg.getTs() : Instant.now());

        if (acc.getStatus() == AccountStatus.FROZEN || acc.getStatus() == AccountStatus.ARRESTED) {
            tx.setStatus(TransactionStatus.BLOCKED);
            txRepo.save(tx);
            log.info("Transaction {} blocked: account {} status {}",
                    msg.getId(), acc.getProductId(), acc.getStatus());
            return;
        }

        if (msg.getCardId() != null && isFraudThresholdExceeded(msg.getCardId(), msg.getTs())) {
            acc.setStatus(AccountStatus.FROZEN);
            accountRepo.save(acc);
            if (card != null) {
                card.setStatus(com.account_processing.enums.CardStatus.FROZEN);
                cardRepo.save(card);
            }
            tx.setStatus(TransactionStatus.BLOCKED);
            txRepo.save(tx);
            log.warn("Fraud detected: card {}. Account {} frozen.", msg.getCardId(), acc.getProductId());
            return;
        }

        BigDecimal delta = deltaByType(msg.getType(), msg.getAmount());
        acc.setBalance(acc.getBalance().add(delta));
        accountRepo.save(acc);

        tx.setStatus(TransactionStatus.COMPLETE);
        txRepo.save(tx);

        if (Boolean.TRUE.equals(acc.getIsRecalc())) {
            recalcPaymentSchedule(acc);
            if (isAccrual(msg.getType())) {
                tryAutopayDuePayment(acc, msg);
            }
        }
    }
    private boolean isFraudThresholdExceeded(String cardId, Instant ts) {
        Instant from = (ts != null ? ts : Instant.now()).minusSeconds(fraudWindowSeconds);
        long count = txRepo.countByCardId_CardIdAndDateAfter(cardId, from);
        return count >= fraudThreshold;
    }

    private boolean isAccrual(TransactionType type) {
        return type == TransactionType.REFUND || type == TransactionType.ADJUSTMENT;
    }

    private BigDecimal deltaByType(TransactionType type, BigDecimal amount) {
        if (amount == null) amount = BigDecimal.ZERO;
        amount = amount.abs();
        return switch (type) {
            case PURCHASE, CASH_WITHDRAWAL, TRANSFER, FEE -> amount.negate();
            case REFUND, ADJUSTMENT -> amount;
        };
    }
    private void recalcPaymentSchedule(Account acc) {
        BigDecimal principal = acc.getBalance().compareTo(BigDecimal.ZERO) < 0
                ? acc.getBalance().abs()
                : BigDecimal.ZERO;
        if (principal.signum() == 0) {
            return;
        }
        BigDecimal annual = acc.getInterestRate() == null ? BigDecimal.ZERO : acc.getInterestRate();
        BigDecimal monthlyRate = annual.divide(BigDecimal.valueOf(12), 10, RoundingMode.HALF_UP);
        int n = 12;
        BigDecimal one = BigDecimal.ONE;
        BigDecimal factor = one.add(monthlyRate).pow(n);
        BigDecimal A = principal.multiply(monthlyRate).multiply(factor)
                .divide(factor.subtract(one), 2, RoundingMode.HALF_UP);

        List<Payment> open = paymentRepo.findByAccountAndPayedAtIsNullOrderByPaymentDateAsc(acc);
        for (Payment p : open) {
            if (p.getPaymentDate().isAfter(LocalDate.now())) {
                paymentRepo.delete(p);
            }
        }
        LocalDate start = LocalDate.now().plusMonths(1).withDayOfMonth(1);
        for (int i = 0; i < n; i++) {
            Payment p = new Payment();
            p.setAccount(acc);
            p.setPaymentDate(start.plusMonths(i));
            p.setAmount(A);
            p.setIsCredit(Boolean.TRUE);
            p.setType(PaymentType.INTEREST);
            p.setExpired(Boolean.FALSE);
            paymentRepo.save(p);
        }
        log.info("Payment schedule recalculated for productId={}", acc.getProductId());
    }
    private void tryAutopayDuePayment(Account acc, ClientTransactionMessage msg) {
        LocalDate txDate = (msg.getTs() != null ? msg.getTs() : Instant.now())
                .atZone(ZoneOffset.UTC).toLocalDate();
        List<Payment> dueList = paymentRepo
                .findByAccountAndPaymentDateLessThanEqualAndPayedAtIsNullOrderByPaymentDateAsc(acc, txDate);
        if (dueList.isEmpty()) return;


        Payment due = dueList.get(0);
        if (acc.getBalance().compareTo(due.getAmount()) >= 0) {
            acc.setBalance(acc.getBalance().subtract(due.getAmount()));
            accountRepo.save(acc);
            due.setPayedAt(Instant.now());
            paymentRepo.save(due);
            log.info("Autopay success for productId={}, amount={}", acc.getProductId(), due.getAmount());
        } else {
            due.setExpired(Boolean.TRUE);
            paymentRepo.save(due);
            log.warn("Autopay skipped (insufficient funds). productId={}, need={}, balance={}",
                    acc.getProductId(), due.getAmount(), acc.getBalance());
        }
    }
}
