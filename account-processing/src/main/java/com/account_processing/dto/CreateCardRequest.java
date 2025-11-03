package com.account_processing.dto;

import com.account_processing.enums.PaymentSystem;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateCardRequest {
    private String clientId;
    private String productId;
    private PaymentSystem paymentSystem;
    private Instant ts;
}
