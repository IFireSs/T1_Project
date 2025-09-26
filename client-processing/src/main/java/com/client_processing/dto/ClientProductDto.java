package com.client_processing.dto;

import com.client_processing.enums.ClientProductStatus;
import com.client_processing.enums.ProductKey;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDate;


@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClientProductDto extends Dto {
    private String clientId;
    private String productId;
    private LocalDate openDate = LocalDate.now();
    private LocalDate closeDate = openDate.plusYears(5);
    private ClientProductStatus status;
}
