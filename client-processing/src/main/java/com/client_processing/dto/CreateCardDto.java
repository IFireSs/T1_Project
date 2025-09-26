package com.client_processing.dto;

import com.client_processing.enums.PaymentSystem;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateCardDto {
    private String clientId;
    private String productId;
    private PaymentSystem paymentSystem;
}
