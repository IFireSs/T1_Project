package com.credit_processing.service;


import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class ClientProcessingClient {
    private final RestTemplate rest;

    @Value("${credit.mc1.base-url}")
    String baseUrl;

    @Data
    public static class ClientBrief {
        private String firstName, middleName, lastName, documentId;
    }

    public Optional<ClientBrief> getBrief(String clientId) {
        try {
            var url = baseUrl + "/api/clients/" + clientId + "/brief";
            var brief = rest.getForObject(url, ClientBrief.class);
            return Optional.ofNullable(brief);
        } catch (Exception e) {
            log.warn("MC-1 brief not available for clientId={}: {}", clientId, e.getMessage());
            return Optional.empty();
        }
    }
}
