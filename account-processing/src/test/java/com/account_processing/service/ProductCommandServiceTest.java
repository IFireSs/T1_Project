package com.account_processing.service;

import com.account_processing.entity.Account;
import com.account_processing.enums.AccountStatus;
import com.account_processing.repository.AccountRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductCommandServiceTest {

    @Mock AccountRepository repo;

    @InjectMocks ProductCommandService service;

    @Test
    void delete_setsClosed_whenAccountFound() {
        var acc = new Account();
        acc.setClientId("C1");
        acc.setProductId("P1");
        acc.setStatus(AccountStatus.OPENED);
        when(repo.findByProductId("P1")).thenReturn(Optional.of(acc));

        service.delete("C1", "P1");

        assertThat(acc.getStatus()).isEqualTo(AccountStatus.CLOSED);
        verify(repo).save(acc);
    }

    @Test
    void delete_doesNothing_whenNotFound() {
        when(repo.findByProductId("NOPE")).thenReturn(Optional.empty());
        service.delete("C1", "NOPE");
        verify(repo, never()).save(any());
    }
}
