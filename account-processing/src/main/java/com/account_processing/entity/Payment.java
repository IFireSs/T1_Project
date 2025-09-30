package com.account_processing.entity;

import com.account_processing.enums.PaymentType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@Table(name = "payments")
public class Payment {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;
    @ManyToOne
    @JoinColumn(name = "account_id")
    private Account account;
    @Column(name = "payment_date")
    private LocalDate paymentDate;
    @Column(name = "amount", precision = 19, scale = 2)
    private BigDecimal amount;
    @Column(name = "is_credit")
    private Boolean isCredit = Boolean.TRUE;
    @Column(name = "payed_at")
    private Instant payedAt;
    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private PaymentType type;
    @Column(name = "expired")
    private Boolean expired = Boolean.FALSE;
}
