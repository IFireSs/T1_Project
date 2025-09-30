package com.account_processing.repository;

import com.account_processing.entity.Account;
import com.account_processing.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByAccountAndPayedAtIsNullOrderByPaymentDateAsc(Account account);
    List<Payment> findByAccountAndPaymentDateLessThanEqualAndPayedAtIsNullOrderByPaymentDateAsc(Account account, LocalDate paymentDate
    );
}