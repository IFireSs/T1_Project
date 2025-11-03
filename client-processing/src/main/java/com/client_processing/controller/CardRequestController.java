package com.client_processing.controller;

import com.client_processing.dto.CreateCardDto;
import com.client_processing.service.CardService;
import com.ms.aspects.annotations.HttpIncomeRequestLog;
import com.ms.aspects.annotations.Metric;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cards")
@RequiredArgsConstructor
public class CardRequestController {
    private final CardService cardService;

    @Metric
    @HttpIncomeRequestLog
    @PostMapping("/create")
    public ResponseEntity<Void> requestCard(@RequestBody CreateCardDto dto) {
        return cardService.requestCard(dto);
    }
}
