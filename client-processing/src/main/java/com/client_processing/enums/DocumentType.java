package com.client_processing.enums;

import lombok.Getter;

@Getter
public enum DocumentType {
    PASSPORT("PASSPORT"),
    INT_PASSPORT("INT_PASSPORT"),
    BIRTH_CERT("BIRTH_CERT");

    private final String status;
    DocumentType(String status) {
        this.status = status;
    }
}
