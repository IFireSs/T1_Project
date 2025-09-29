package com.account_processing.enums;

import lombok.Getter;

@Getter
public enum PaymentType {
    CASH_IN("CASH_IN"),
    CASH_OUT("CASH_OUT"),
    TRANSFER_IN("TRANSFER_IN"),
    TRANSFER_OUT("TRANSFER_OUT"),
    FEE("FEE"),
    INTEREST("INTEREST");

    private final String value;

    PaymentType(String value) {
        this.value = value;
    }
}