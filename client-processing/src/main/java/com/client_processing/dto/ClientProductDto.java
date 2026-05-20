package com.client_processing.dto;

import com.client_processing.enums.ClientProductStatus;
import jakarta.validation.constraints.NotBlank;
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
    @NotBlank
    private String clientId;
    @NotBlank
    private String productId;
    @Builder.Default
    private LocalDate openDate = LocalDate.now();
    @Builder.Default
    private LocalDate closeDate = LocalDate.now().plusYears(5);
    @Builder.Default
    private ClientProductStatus status = ClientProductStatus.ACTIVE;
}
