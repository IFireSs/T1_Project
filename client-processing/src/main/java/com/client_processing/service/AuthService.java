package com.client_processing.service;

import com.client_processing.dto.LoginRequest;
import com.client_processing.dto.LoginResponse;
import com.client_processing.mapper.UserMapper;
import com.client_processing.repository.ClientRepository;
import com.client_processing.repository.UserRepository;
import com.client_processing.security.JwtService;
import com.client_processing.security.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final ClientRepository clientRepository;
    private final UserMapper userMapper;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    public LoginResponse login(LoginRequest request) {
        var user = userRepository.findByLogin(request.getLogin())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid login or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid login or password");
        }

        var client = clientRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Client profile not found"));
        var roles = rolesFor(user.getLogin());
        var token = jwtService.generateForClient(client.getClientId(), roles);

        return LoginResponse.builder()
                .clientId(client.getClientId())
                .token(token)
                .roles(roles)
                .user(userMapper.toDto(user))
                .build();
    }

    private List<Role> rolesFor(String login) {
        if ("master".equalsIgnoreCase(login)) {
            return List.of(Role.MASTER);
        }
        return List.of(Role.CURRENT_CLIENT);
    }
}
