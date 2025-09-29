package com.client_processing.enums;

import lombok.Getter;

@Getter
public enum ProductKey {
    DC("DC"),
    CC("CC"),
    AC("AC"),
    IPO("IPO"),
    PC("PC"),
    PENS("PENS"),
    NS("NS"),
    INS("INS"),
    BS("BS");

    private final String value;

    ProductKey(String value) {
        this.value = value;
    }
}