package com.account_processing.enums;

import lombok.Getter;

@Getter
public enum ClientProductStatus {
    OPENED("OPENED"),
    ACTIVE("ACTIVE"),
    CLOSED("CLOSED"),
    FROZEN("FROZEN"),
    ARRESTED("ARRESTED");

    private final String status;
    ClientProductStatus(String status) {
        this.status = status;
    }
}
