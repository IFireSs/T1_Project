package com.credit_processing.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClientProductMessage {
    public enum Op { CREATE, UPDATE, DELETE }
    private Op op;
    private String clientId;
    private String productId;
    private LocalDate openDate;
    private LocalDate closeDate;
}
