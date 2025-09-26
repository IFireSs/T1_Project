package com.credit_processing.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@Table(name = "product_registry")
public class ProductRegistry {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;
    @Column(name = "client_id")
    private String clientId;
    @Column(name = "account_id")
    private Long accountId = 0L;
    @Column(name = "product_id")
    private String productId;
    @Column(name = "interest_rate")
    private BigDecimal interestRate;
    @Column(name = "open_date")
    private LocalDate openDate;
    @Column(name = "month_count")
    private Integer monthCount;
    @Column(name = "principal_amount")
    private BigDecimal principalAmount;
}
