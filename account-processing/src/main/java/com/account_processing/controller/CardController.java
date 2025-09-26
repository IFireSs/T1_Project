package com.account_processing.controller;

import com.account_processing.dto.CardDto;
import com.account_processing.mapper.CardMapper;
import com.account_processing.repository.AccountRepository;
import com.account_processing.repository.CardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/cards")
@RequiredArgsConstructor
public class CardController {
    private final CardRepository repo;
    private final CardMapper mapper;
    private final AccountRepository accountRepo;

}
