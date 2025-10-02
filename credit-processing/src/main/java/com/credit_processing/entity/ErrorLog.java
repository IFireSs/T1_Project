package com.credit_processing.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "error_log")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErrorLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Instant timestamp;

    @Column(length = 512)
    private String methodSignature;

    @Column(length = 4000)
    private String exceptionMessage;

    @Column(length = 8000)
    private String stacktrace;

    @Column(length = 4000)
    private String paramsJson;

    @Column(length = 128)
    private String serviceName;

    @Column(length = 32)
    private String type;
}
