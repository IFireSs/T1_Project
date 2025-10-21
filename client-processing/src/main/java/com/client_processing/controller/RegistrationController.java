package com.client_processing.controller;

import com.client_processing.dto.ClientDto;
import com.client_processing.dto.RegistrationRequest;
import com.client_processing.dto.RegistrationResponse;
import com.client_processing.service.RegistrationService;
import com.ms.aspects.annotations.HttpIncomeRequestLog;
import com.ms.aspects.annotations.Metric;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/clients")
@RequiredArgsConstructor
public class RegistrationController {
    private final RegistrationService registrationService;

    @Metric
    @HttpIncomeRequestLog
    @PostMapping("/register")
    public ResponseEntity<RegistrationResponse> register(@RequestBody RegistrationRequest req) {
        return ResponseEntity.ok(registrationService.register(req));
    }

    @Metric
    @HttpIncomeRequestLog
    @GetMapping("/{clientId}/brief")
    public ResponseEntity<ClientDto> brief(@PathVariable String clientId) {
        return registrationService.brief(clientId);
    }
}
