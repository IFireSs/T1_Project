package com.account_processing.enums;

import lombok.Getter;

@Getter
public enum TransactionType {
    PURCHASE("PURCHASE"),
    CASH_WITHDRAWAL("CASH_WITHDRAWAL"),
    TRANSFER("TRANSFER"),
    REFUND("REFUND"),
    FEE("FEE"),
    ADJUSTMENT("ADJUSTMENT");

    private final String value;

    TransactionType(String value) {
        this.value = value;
    }
}