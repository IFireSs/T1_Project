package com.account_processing.enums;

import lombok.Getter;

@Getter
public enum TransactionStatus {
    ALLOWED("ALLOWED"),
    PROCESSING("PROCESSING"),
    COMPLETE("COMPLETE"),
    BLOCKED("BLOCKED"),
    CANCELLED("CANCELLED");

    private final String value;

    TransactionStatus(String value) {
        this.value = value;
    }
}