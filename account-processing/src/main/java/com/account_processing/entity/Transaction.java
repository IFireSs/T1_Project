package com.account_processing.entity;


import com.account_processing.enums.TransactionStatus;
import com.account_processing.enums.TransactionType;
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

@Entity
@Getter
@Setter
@Table(name = "transactions")
public class Transaction {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long Id;
    @ManyToOne
    @JoinColumn(name = "account_id")
    Account account;
    @ManyToOne
    @JoinColumn(name = "card_id")
    Card cardId;
    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    TransactionType type;
    @Column(name = "amount", precision = 19, scale = 2)
    BigDecimal amount;
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    TransactionStatus status;
    @Column(name = "timestamp")
    Instant date;
}
