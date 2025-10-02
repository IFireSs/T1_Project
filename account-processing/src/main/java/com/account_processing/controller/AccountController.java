package com.account_processing.controller;

import com.account_processing.aspect.annotations.HttpIncomeRequestLog;
import com.account_processing.dto.AccountDto;
import com.account_processing.entity.Account;
import com.account_processing.mapper.AccountMapper;
import com.account_processing.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class AccountController {
    private final AccountRepository accountRepository;
    private final AccountMapper mapper;

    @GetMapping("/by-client/{clientId}")
    @HttpIncomeRequestLog
    public ResponseEntity<List<AccountDto>> findByClient(@PathVariable String clientId) {
        List<Account> list = accountRepository.findAllByClientId(clientId);
        return ResponseEntity.ok(mapper.toDtoList(list));
    }

    @GetMapping("/{clientId}/{productId}")
    @HttpIncomeRequestLog
    public ResponseEntity<AccountDto> findOne(@PathVariable String clientId, @PathVariable String productId) {
        var account = accountRepository.findTopByClientIdAndProductIdOrderByIdDesc(clientId, productId);
        if (!account.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(mapper.toDto(account.get()));
    }
}
