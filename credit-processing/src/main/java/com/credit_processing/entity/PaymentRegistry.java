package com.credit_processing.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@Table(name = "payment_registry")
public class PaymentRegistry {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;

    @ManyToOne
    @JoinColumn(name = "product_registry_id")
    private ProductRegistry productRegistryId;
    @Column(name = "payment_date")
    private LocalDate paymentDate;
    @Column(name = "amount")
    private BigDecimal  amount;
    @Column(name = "interest_rate_amount")
    private BigDecimal interestRateAmount;
    @Column(name = "debt_amount")
    private BigDecimal  debtAmount;
    @Column(name = "expired")
    private Boolean expired = Boolean.FALSE;
    @Column(name = "payment_expiration_date")
    private LocalDate paymentExpirationDate;
}
