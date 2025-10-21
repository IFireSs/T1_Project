package com.client_processing.security;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Configuration
@EnableMethodSecurity
@EnableConfigurationProperties(JwtProperties.class)
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   JwtAuthFilter jwtFilter) throws Exception {
        http.csrf(csrf -> csrf.disable())
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(reg -> reg
                        .requestMatchers("/actuator/**", "/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                        .requestMatchers("/api/clients/register").permitAll()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public JwtAuthFilter jwtFilter(JwtService jwt,
                                   org.springframework.beans.factory.ObjectProvider<BlocklistGuard> guardProvider) {
        BlocklistGuard guard = guardProvider.getIfAvailable();
        return (guard != null) ? new JwtAuthFilter(jwt, guard) : new JwtAuthFilter(jwt);
    }

    @Bean
    @Primary
    public RestTemplate restTemplate(JwtService jwt) {
        RestTemplate rt = new RestTemplate();
        ClientHttpRequestInterceptor auth = (req, body, exec) -> {
            if (!req.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                req.getHeaders().add(HttpHeaders.AUTHORIZATION, "Bearer " + jwt.generateForService());
            }
            return exec.execute(req, body);
        };
        rt.setInterceptors(List.of(auth));
        return rt;
    }

}