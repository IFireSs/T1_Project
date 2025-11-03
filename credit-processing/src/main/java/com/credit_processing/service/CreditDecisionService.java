package com.credit_processing.service;

import com.credit_processing.dto.ClientProductMessage;
import com.credit_processing.entity.PaymentRegistry;
import com.credit_processing.entity.ProductRegistry;
import com.credit_processing.repository.PaymentRegistryRepo;
import com.credit_processing.repository.ProductRegistryRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.LocalDate;

@Slf4j
@Service
@RequiredArgsConstructor
public class CreditDecisionService {
    private final ProductRegistryRepo productRepo;
    private final PaymentRegistryRepo paymentRepo;
    private final ClientProcessingClient mc1;

    @Value("${credit.decision.limit}")
    private BigDecimal limitN;
    @Value("${credit.defaults.annualRate}")
    private BigDecimal defaultAnnualRate;
    @Value("${credit.defaults.monthCount}")
    private Integer defaultMonthCount;
    @Value("${credit.defaults.amount}")
    private BigDecimal defaultAmount;

    @Transactional
    public void handle(ClientProductMessage msg) {
        if (msg.getOp() != ClientProductMessage.Op.CREATE) {
            log.info("Skip non-CREATE op: {}", msg.getOp());
            return;
        }

        mc1.getBrief(msg.getClientId()).ifPresent(b ->
                log.info("MC-1 brief: {} {} {}, doc={}", b.getLastName(), b.getFirstName(), b.getMiddleName(), b.getDocumentId())
        );

        String clientId = msg.getClientId();
        Long accountId = 0L;
        String productId = msg.getProductId();

        BigDecimal requestAmount = defaultAmount;
        BigDecimal annualRate    = defaultAnnualRate;
        int months               = defaultMonthCount;
        LocalDate openDate       = msg.getOpenDate() != null ? msg.getOpenDate() : LocalDate.now();

        BigDecimal exposure = productRepo.sumPrincipalByClientId(clientId);
        if (exposure.add(requestAmount).compareTo(limitN) > 0) {
            log.info("DECLINE: exposure {} + req {} > limit {}", exposure, requestAmount, limitN);
            return;
        }

        boolean hasOverdues = paymentRepo.existsByProductRegistryId_ClientIdAndExpiredTrue(clientId);
        if (hasOverdues) {
            log.info("DECLINE: clientId {} has overdue payments", clientId);
            return;
        }

        ProductRegistry pr = new ProductRegistry();
        pr.setClientId(clientId);
        pr.setAccountId(accountId);
        pr.setProductId(productId);
        pr.setInterestRate(annualRate);
        pr.setOpenDate(openDate);
        pr.setMonthCount(months);
        pr.setPrincipalAmount(requestAmount);
        pr = productRepo.save(pr);

        generateSchedule(pr);
        log.info("APPROVED: opened productRegistry id={} for clientId={}", pr.getId(), clientId);
    }

    private void generateSchedule(ProductRegistry pr) {
        BigDecimal S = pr.getPrincipalAmount();
        int n = pr.getMonthCount();
        BigDecimal iMonthly = pr.getInterestRate().divide(BigDecimal.valueOf(12), MathContext.DECIMAL64);

        BigDecimal onePlusI = BigDecimal.ONE.add(iMonthly, MathContext.DECIMAL64);
        BigDecimal pow = onePlusI.pow(n, MathContext.DECIMAL64);
        BigDecimal numerator = S.multiply(iMonthly, MathContext.DECIMAL64).multiply(pow, MathContext.DECIMAL64);
        BigDecimal denominator = pow.subtract(BigDecimal.ONE, MathContext.DECIMAL64);
        BigDecimal A = numerator.divide(denominator, 10, RoundingMode.HALF_UP);
        A = A.setScale(2, RoundingMode.HALF_UP);

        BigDecimal balance = S;
        for (int k = 1; k <= n; k++) {
            BigDecimal interest = balance.multiply(iMonthly).setScale(2, RoundingMode.HALF_UP);
            BigDecimal principal = A.subtract(interest).setScale(2, RoundingMode.HALF_UP);
            balance = balance.subtract(principal).max(BigDecimal.ZERO).setScale(2, RoundingMode.HALF_UP);

            PaymentRegistry row = new PaymentRegistry();
            row.setProductRegistryId(pr);
            row.setPaymentDate(pr.getOpenDate().plusMonths(k));
            row.setAmount(A);
            row.setInterestRateAmount(interest);
            row.setDebtAmount(balance);
            row.setExpired(Boolean.FALSE);
            row.setPaymentExpirationDate(row.getPaymentDate().plusDays(5));
            paymentRepo.save(row);
        }
    }
}
