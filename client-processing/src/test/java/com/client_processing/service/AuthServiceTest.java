package com.client_processing.service;

import com.client_processing.dto.LoginRequest;
import com.client_processing.dto.UserDto;
import com.client_processing.entity.Client;
import com.client_processing.entity.User;
import com.client_processing.mapper.UserMapper;
import com.client_processing.repository.ClientRepository;
import com.client_processing.repository.UserRepository;
import com.client_processing.security.JwtService;
import com.client_processing.security.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock UserRepository userRepository;
    @Mock ClientRepository clientRepository;
    @Mock UserMapper userMapper;
    @Mock JwtService jwtService;
    PasswordEncoder passwordEncoder;

    AuthService authService;

    @BeforeEach
    void setUp() {
        passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
        authService = new AuthService(userRepository, clientRepository, userMapper, jwtService, passwordEncoder);
    }

    @Test
    void login_returnsMasterTokenForSeedMasterUser() {
        var user = new User();
        user.setId(10L);
        user.setLogin("master");
        user.setPassword("{noop}master");
        user.setEmail("master@example.com");

        var client = new Client();
        client.setClientId("770000000099");

        when(userRepository.findByLogin("master")).thenReturn(Optional.of(user));
        when(clientRepository.findByUserId(10L)).thenReturn(Optional.of(client));
        when(jwtService.generateForClient("770000000099", List.of(Role.MASTER))).thenReturn("jwt");
        when(userMapper.toDto(user)).thenReturn(UserDto.builder().login("master").email("master@example.com").build());

        var response = authService.login(LoginRequest.builder()
                .login("master")
                .password("master")
                .build());

        assertThat(response.getClientId()).isEqualTo("770000000099");
        assertThat(response.getToken()).isEqualTo("jwt");
        assertThat(response.getRoles()).containsExactly(Role.MASTER);
    }
}
