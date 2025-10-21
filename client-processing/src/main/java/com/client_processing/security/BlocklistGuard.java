package com.client_processing.security;

import com.client_processing.repository.BlacklistRepository;
import com.client_processing.repository.ClientRepository;
import com.client_processing.entity.Client;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@ConditionalOnBean({ClientRepository.class, BlacklistRepository.class})
public class BlocklistGuard {
    private final ClientRepository clientRepository;
    private final BlacklistRepository blacklistRepository;

    public boolean isBlocked(String clientId) {
        return clientRepository.findByClientId(clientId)
            .map(this::isClientBlocked)
            .orElse(false);
    }

    private boolean isClientBlocked(Client c) {
        if (c.getDocumentId() != null && blacklistRepository.existsByDocumentId(c.getDocumentId())) return true;
        try {
            var emailField = c.getClass().getDeclaredField("email");
            emailField.setAccessible(true);
            Object email = emailField.get(c);
            if (email instanceof String && blacklistRepository.existsByEmail((String) email)) return true;
        } catch (Exception ignored) { }
        try {
            var phoneField = c.getClass().getDeclaredField("phone");
            phoneField.setAccessible(true);
            Object phone = phoneField.get(c);
            if (phone instanceof String && blacklistRepository.existsByPhone((String) phone)) return true;
        } catch (Exception ignored) { }
        return false;
    }
}
