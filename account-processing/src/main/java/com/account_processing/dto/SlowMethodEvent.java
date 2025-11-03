package com.account_processing.dto;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class SlowMethodEvent {
    private Instant timestamp;
    private String method;
    private Long durationMs;
    private Object[] params;
}
