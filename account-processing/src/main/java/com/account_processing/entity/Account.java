package com.account_processing.entity;

import com.account_processing.enums.AccountStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
@Table(name = "accounts")
public class Account {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;
    @Column(name = "client_id")
    private String clientId;
    @Column(name = "product_id", nullable = false, unique = true)
    private String productId;
    @Column(name = "balance")
    private BigDecimal balance;
    @Column(name = "interest_rate", precision = 9, scale = 6)
    private BigDecimal interestRate;
    @Column(name = "is_recalc")
    private Boolean isRecalc = Boolean.FALSE;
    @Column(name = "card_exist")
    private Boolean cardExist = Boolean.FALSE;
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private AccountStatus status = AccountStatus.ACTIVE;
}
