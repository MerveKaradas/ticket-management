package com.kafein.ticket_management.config;

import java.nio.charset.StandardCharsets;

import javax.crypto.SecretKey;

import org.springframework.boot.context.properties.ConfigurationProperties;

import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@ConfigurationProperties(prefix = "spring.application.security.jwt")
@RequiredArgsConstructor
@Getter
public class JwtProperties {
    private final String secret;
    private final long jwtExpirationInMs;
    private final long refreshTokenExpirationInMs;


    public SecretKey getSecretKey(){
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        
    }
}
