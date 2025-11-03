package com.client_processing.security;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "security.jwt")
public class JwtProperties {
    private String issuer;
    private String secret;
    private long ttlSec;
    private long serviceTtlSec;
    private String serviceId;

    public String getIssuer() {
        return issuer;
    }

    public void setIssuer(String issuer) {
        this.issuer = issuer;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public long getTtlSec() {
        return ttlSec;
    }

    public void setTtlSec(long ttlSec) {
        this.ttlSec = ttlSec;
    }

    public long getServiceTtlSec() {
        return serviceTtlSec;
    }

    public void setServiceTtlSec(long serviceTtlSec) {
        this.serviceTtlSec = serviceTtlSec;
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }
}
