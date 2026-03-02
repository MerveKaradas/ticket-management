package com.kafein.ticket_management.config;

import java.nio.charset.StandardCharsets;

import javax.crypto.SecretKey;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import lombok.Setter;

@Component
@ConfigurationProperties(prefix = "spring.application.security.jwt")
@Getter
@Setter
public class JwtProperties {
    private String secret;
    private long jwtExpirationInMs;
    private long refreshTokenExpirationInMs;

    public SecretKey getSecretKey(){
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));

    }
}
