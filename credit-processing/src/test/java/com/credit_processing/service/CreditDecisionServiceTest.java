package com.credit_processing.service;

import com.credit_processing.dto.ClientProductMessage;
import com.credit_processing.entity.PaymentRegistry;
import com.credit_processing.entity.ProductRegistry;
import com.credit_processing.repository.PaymentRegistryRepo;
import com.credit_processing.repository.ProductRegistryRepo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreditDecisionServiceTest {

    @Mock ProductRegistryRepo productRepo;
    @Mock PaymentRegistryRepo paymentRepo;
    @Mock ClientProcessingClient clientProcessingClient;

    @InjectMocks CreditDecisionService service;

    @Test
    void handle_skipsDuplicateCreateByProductId() {
        var msg = ClientProductMessage.builder()
                .op(ClientProductMessage.Op.CREATE)
                .clientId("000000000123")
                .productId("PC1")
                .build();
        when(clientProcessingClient.getBrief("000000000123")).thenReturn(Optional.empty());
        when(productRepo.existsByProductId("PC1")).thenReturn(true);

        service.handle(msg);

        verify(productRepo, never()).save(any(ProductRegistry.class));
        verify(paymentRepo, never()).save(any(PaymentRegistry.class));
    }
}
