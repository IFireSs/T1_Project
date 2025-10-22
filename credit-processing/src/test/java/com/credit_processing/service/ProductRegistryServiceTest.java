package com.credit_processing.service;

import com.credit_processing.entity.ProductRegistry;
import com.credit_processing.repository.ProductRegistryRepo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductRegistryServiceTest {

    @Mock ProductRegistryRepo repo;
    @InjectMocks ProductRegistryService service;

    @Test
    void findProductRegistry_returnsRepoData() {
        var list = List.of(new ProductRegistry(), new ProductRegistry());
        when(repo.findAll()).thenReturn(list);

        assertThat(service.findProductRegistry()).isEqualTo(list);
    }
}
