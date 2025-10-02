package com.client_processing.service;

import com.client_processing.aspect.annotations.LogDatasourceError;
import com.client_processing.dto.ClientDto;
import com.client_processing.dto.RegistrationRequest;
import com.client_processing.dto.RegistrationResponse;
import com.client_processing.entity.Client;
import com.client_processing.entity.User;
import com.client_processing.mapper.ClientMapper;
import com.client_processing.mapper.UserMapper;
import com.client_processing.repository.ClientRepository;
import com.client_processing.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RegistrationService {
    private final ClientRepository clientRepo;
    private final UserRepository userRepo;
    private final BlacklistService blacklist;
    private final UserMapper userMapper;
    private final ClientMapper clientMapper;

    @LogDatasourceError
    @Transactional
    public RegistrationResponse register(RegistrationRequest req) {
        blacklist.check(req);

        var user = userRepo.save(User.builder()
                .login(req.getLogin())
                .password(req.getPassword())
                .email(req.getEmail())
                .build());

        var client = clientRepo.save(Client.builder()
                .clientId(req.getClientCode())
                .userId(user.getId())
                .firstName(req.getFirstName())
                .middleName(req.getMiddleName())
                .lastName(req.getLastName())
                .dateOfBirth(req.getDateOfBirth())
                .documentType(req.getDocumentType())
                .documentId(req.getDocumentId())
                .documentPrefix(req.getDocumentPrefix())
                .documentSuffix(req.getDocumentSuffix())
                .build());

        return RegistrationResponse.builder()
                .clientId(client.getClientId())
                .user(userMapper.toDto(user))
                .build();
    }

    @LogDatasourceError
    @Transactional
    public ResponseEntity<ClientDto> brief(String clientId) {
        //todo: допилить проверку
        return ResponseEntity.ok(clientMapper.toDto(clientRepo.findByClientId(clientId).orElseThrow()));
    }
}