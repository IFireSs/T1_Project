package com.client_processing.controller;

import com.client_processing.dto.ProductDto;
import com.client_processing.service.ProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ProductController.class)
@AutoConfigureMockMvc(addFilters = false)
class ProductControllerWebTest {

    @Autowired
    MockMvc mvc;
    @MockitoBean
    ProductService service;

    @Test
    void list_returnsArray() throws Exception {
        when(service.list()).thenReturn(org.springframework.http.ResponseEntity.ok(List.of(
                ProductDto.builder().name("Debit Card").productId("DC1").build(),
                ProductDto.builder().name("Credit Card").productId("CC2").build()
        )));

        mvc.perform(get("/api/products").accept(MediaType.APPLICATION_JSON))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$[0].name").value("Debit Card"))
           .andExpect(jsonPath("$[1].productId").value("CC2"));
    }
}
