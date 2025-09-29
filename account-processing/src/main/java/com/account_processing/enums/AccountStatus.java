package com.account_processing.enums;

import lombok.Getter;

@Getter
public enum AccountStatus {
    OPENED("OPENED"),
    ACTIVE("ACTIVE"),
    CLOSED("CLOSED"),
    FROZEN("FROZEN"),
    ARRESTED("ARRESTED");

    private final String status;
    AccountStatus(String status) {
        this.status = status;
    }
}
