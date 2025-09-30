package com.account_processing.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClientPaymentMessage {
    private UUID id;
    private String clientId;
    private String productId;
    private BigDecimal amount;
    private Instant ts;
}