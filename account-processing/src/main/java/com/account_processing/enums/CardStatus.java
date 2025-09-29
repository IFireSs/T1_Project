package com.account_processing.enums;

import lombok.Getter;

@Getter
public enum CardStatus {
    OPENED("OPENED"),
    ACTIVE("ACTIVE"),
    CLOSED("CLOSED"),
    FROZEN("FROZEN"),
    ARRESTED("ARRESTED");

    private final String status;
    CardStatus(String status) {
        this.status = status;
    }
}
