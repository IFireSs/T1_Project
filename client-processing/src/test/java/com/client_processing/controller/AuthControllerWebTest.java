package com.client_processing.controller;

import com.client_processing.dto.LoginRequest;
import com.client_processing.dto.LoginResponse;
import com.client_processing.dto.UserDto;
import com.client_processing.security.Role;
import com.client_processing.service.AuthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerWebTest {

    @Autowired MockMvc mvc;
    @MockitoBean AuthService authService;

    @Test
    void login_returnsToken() throws Exception {
        when(authService.login(any(LoginRequest.class))).thenReturn(LoginResponse.builder()
                .clientId("770000000099")
                .token("jwt")
                .roles(List.of(Role.MASTER))
                .user(UserDto.builder().login("master").email("master@example.com").build())
                .build());

        mvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "login": "master",
                                  "password": "master"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.clientId").value("770000000099"))
                .andExpect(jsonPath("$.token").value("jwt"))
                .andExpect(jsonPath("$.roles[0]").value("MASTER"));
    }
}
