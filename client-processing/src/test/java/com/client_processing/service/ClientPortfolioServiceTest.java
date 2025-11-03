package com.client_processing.service;

import com.client_processing.dto.ClientProductDto;
import com.client_processing.dto.kafka.ClientProductMessage;
import com.client_processing.kafka.ClientKafkaProducer;
import com.client_processing.mapper.ClientProductMapper;
import com.client_processing.mapper.KafkaMapper;
import com.client_processing.repository.ClientProductRepository;
import com.client_processing.repository.ClientRepository;
import com.client_processing.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClientPortfolioServiceTest {

    @Mock ClientProductRepository repo;
    @Mock ClientRepository clientRepo;
    @Mock ProductRepository productRepo;
    @Mock ClientProductMapper mapper;
    @Mock KafkaMapper kafkaMapper;
    @Mock ClientKafkaProducer producer;

    @InjectMocks ClientPortfolioService service;

    @Test
    void delete_sendsKafkaAndDeletes_whenProductExists() {
        var dto = ClientProductDto.builder().clientId("C1").productId("P1").build();
        when(repo.findByProductId("P1")).thenReturn(Optional.of(new com.client_processing.entity.ClientProduct()));
        var msg = new ClientProductMessage();
        when(kafkaMapper.toClientProductMessage(eq(dto), eq(ClientProductMessage.Op.DELETE))).thenReturn(msg);

        service.delete(dto);

        verify(repo).deleteByProductId("P1");
        verify(producer).publishClientProduct(msg);
    }

    @Test
    void delete_doesNothing_whenProductMissing() {
        var dto = ClientProductDto.builder().clientId("C1").productId("NOPE").build();
        when(repo.findByProductId("NOPE")).thenReturn(Optional.empty());

        service.delete(dto);

        verify(repo, never()).deleteByProductId(any());
        verifyNoInteractions(producer);
    }
}
