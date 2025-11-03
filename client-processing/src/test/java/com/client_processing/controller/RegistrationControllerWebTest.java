package com.client_processing.controller;

import com.client_processing.dto.ClientDto;
import com.client_processing.dto.RegistrationRequest;
import com.client_processing.dto.RegistrationResponse;
import com.client_processing.dto.UserDto;
import com.client_processing.service.RegistrationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = RegistrationController.class)
@AutoConfigureMockMvc(addFilters = false)
class RegistrationControllerWebTest {

    @Autowired MockMvc mvc;
    @MockitoBean
    RegistrationService registrationService;

    @Test
    void register_returnsOk() throws Exception {
        var resp = RegistrationResponse.builder()
                .clientId("C123")
                .token("jwt")
                .user(UserDto.builder().login("Ann").email("a@b.c").build())
                .build();
        when(registrationService.register(any(RegistrationRequest.class))).thenReturn(resp);

        String body = "{\"login\":\"ann\",\"password\":\"p\",\"email\":\"a@b.c\",}";

        mvc.perform(post("/api/clients/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
           .andExpect(status().isOk())
           .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
           .andExpect(jsonPath("$.clientId").value("C123"))
           .andExpect(jsonPath("$.user.firstName").value("Ann"));
    }
}
