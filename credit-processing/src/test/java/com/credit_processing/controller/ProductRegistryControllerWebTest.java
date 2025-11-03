package com.credit_processing.controller;

import com.credit_processing.entity.ProductRegistry;
import com.credit_processing.service.ProductRegistryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ProductRegistryController.class)
@AutoConfigureMockMvc(addFilters = false)
class ProductRegistryControllerWebTest {

    @Autowired MockMvc mvc;
    @MockitoBean
    ProductRegistryService service;

    @Test
    void getAccounts_returnsOk() throws Exception {
        when(service.findProductRegistry()).thenReturn(List.of(new ProductRegistry()));

        mvc.perform(get("/cred").accept(MediaType.APPLICATION_JSON))
           .andExpect(status().isOk());
    }
}
