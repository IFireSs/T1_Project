package com.client_processing.service;

import com.client_processing.dto.RegistrationRequest;
import com.client_processing.dto.UserDto;
import com.client_processing.entity.Client;
import com.client_processing.entity.User;
import com.client_processing.mapper.ClientMapper;
import com.client_processing.mapper.UserMapper;
import com.client_processing.repository.ClientRepository;
import com.client_processing.repository.UserRepository;
import com.client_processing.security.JwtService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RegistrationServiceTest {

    @Mock JwtService jwt;
    @Mock ClientRepository clientRepo;
    @Mock UserRepository userRepo;
    @Mock BlacklistService blacklist;
    @Mock UserMapper userMapper;
    @Mock ClientMapper clientMapper;
    @Mock PasswordEncoder passwordEncoder;

    @InjectMocks RegistrationService service;

    @Test
    void register_savesEncodedPassword() {
        var req = RegistrationRequest.builder()
                .clientCode("000000000123")
                .login("ann")
                .password("password123")
                .email("ann@example.com")
                .firstName("Ann")
                .lastName("Smith")
                .dateOfBirth(LocalDate.of(1990, 1, 1))
                .build();
        when(passwordEncoder.encode("password123")).thenReturn("hashed-password");
        when(userRepo.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));
        when(clientRepo.save(any(Client.class))).thenAnswer(inv -> inv.getArgument(0));
        when(jwt.generateForClient(any(), any())).thenReturn("jwt");
        when(userMapper.toDto(any(User.class))).thenReturn(UserDto.builder().login("ann").build());

        service.register(req);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepo).save(userCaptor.capture());
        assertThat(userCaptor.getValue().getPassword()).isEqualTo("hashed-password");
    }
}
