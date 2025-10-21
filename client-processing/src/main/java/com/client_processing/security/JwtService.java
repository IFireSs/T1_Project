package com.client_processing.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.*;

@Service
public class JwtService {
    private final JwtProperties props;
    private SecretKey key;

    public JwtService(JwtProperties props) {
        this.props = props;
        this.key = Keys.hmacShaKeyFor(props.getSecret().getBytes(StandardCharsets.UTF_8));
    }

    public String generateForClient(String clientId, Collection<Role> roles) {
        return generate(clientId, roles, props.getTtlSec());
    }

    public String generateForService() {
        return generate(props.getServiceId(), List.of(Role.SERVICE), props.getServiceTtlSec());
    }

    private String generate(String subject, Collection<Role> roles, long ttlSec) {
        Instant now = Instant.now();
        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", String.join(",", rolesToStrings(roles)));
        return Jwts.builder()
                .setIssuer(props.getIssuer())
                .setSubject(subject)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plusSeconds(ttlSec)))
                .addClaims(claims)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public Claims parse(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
    }

    public static Collection<String> rolesToStrings(Collection<Role> roles) {
        List<String> list = new ArrayList<>();
        for (Role r : roles) list.add(r.name());
        return list;
    }

    public static Collection<Role> stringsToRoles(String str) {
        if (str == null || str.isBlank()) return List.of();
        String[] arr = str.split(",");
        List<Role> out = new ArrayList<>();
        for (String s : arr) {
            try {
                out.add(Role.valueOf(s.trim()));
            } catch (Exception ignored) {
            }
        }
        return out;
    }
}
