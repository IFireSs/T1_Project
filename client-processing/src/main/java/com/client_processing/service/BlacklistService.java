package com.client_processing.service;

import com.client_processing.dto.RegistrationRequest;
import com.client_processing.repository.BlacklistRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BlacklistService {
    private final BlacklistRepository repo;

    public void check(RegistrationRequest req) {
        if (req.getEmail()!=null && repo.existsByEmail(req.getEmail()))
            throw new IllegalStateException("Email is blacklisted");
        if (req.getDocumentId()!=null && repo.existsByDocumentId(req.getDocumentId()))
            throw new IllegalStateException("Document is blacklisted");
    }
}