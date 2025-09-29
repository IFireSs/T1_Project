package com.account_processing.enums;

import lombok.Getter;

@Getter
public enum PaymentSystem {
    VISA("VISA"),
    MASTERCARD("MASTERCARD"),
    MIR("MIR");

    private final String value;

    PaymentSystem(String value) {
        this.value = value;
    }
}