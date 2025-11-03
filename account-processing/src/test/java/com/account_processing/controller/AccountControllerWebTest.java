package com.account_processing.controller;

import com.account_processing.dto.AccountDto;
import com.account_processing.entity.Account;
import com.account_processing.mapper.AccountMapper;
import com.account_processing.repository.AccountRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = AccountController.class)
@AutoConfigureMockMvc(addFilters = false)
class AccountControllerWebTest {

    @Autowired MockMvc mvc;
    @MockitoBean
    AccountRepository repo;
    @MockitoBean
    AccountMapper mapper;

    @Test
    void findOne_404_whenMissing() throws Exception {
        when(repo.findTopByClientIdAndProductIdOrderByIdDesc(anyString(), anyString())).thenReturn(Optional.empty());

        mvc.perform(get("/api/accounts/C1/P1").accept(MediaType.APPLICATION_JSON))
           .andExpect(status().isNotFound());
    }
}
