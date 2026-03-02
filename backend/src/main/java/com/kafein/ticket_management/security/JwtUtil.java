package com.kafein.ticket_management.security;

import java.util.Date;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.kafein.ticket_management.config.JwtProperties;
import com.kafein.ticket_management.model.User;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

@Component
public class JwtUtil {

    private final JwtProperties jwtProperties;

    public JwtUtil(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
    }

    // JWT 
    public String generateToken(User user){

        return Jwts.builder()
                .setId(UUID.randomUUID().toString())
                .setSubject(user.getId().toString())
                .claim("email",user.getEmail())
                .claim("role", user.getRole().name())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtProperties.getJwtExpirationInMs()))
                .signWith(jwtProperties.getSecretKey())
                .compact();
        
    }

    public String generateRefreshToken(User user) {
        return Jwts.builder()
                .setId(UUID.randomUUID().toString())
                .setSubject(user.getId().toString())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtProperties.getRefreshTokenExpirationInMs()))
                .signWith(jwtProperties.getSecretKey())
                .compact();
    }

    // Token icerisinden subject(user id) bilgisini aliyoruz
    // public UUID getUserIdFromToken(String token) {
    //     return UUID.fromString(Jwts.parserBuilder()
    //             .setSigningKey(jwtProperties.getSecretKey())
    //             .build()
    //             .parseClaimsJws(token)
    //             .getBody()
    //             .getSubject());
    // }

    public String getUserEmailFromToken(String token) {
        return String.valueOf(Jwts.parserBuilder()
                .setSigningKey(jwtProperties.getSecretKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get("email"));
    }

     // Token'ın içindeki tüm claim'leri döner
    public Claims parseClaims(String token) {
        return Jwts.parserBuilder()
                   .setSigningKey(jwtProperties.getSecretKey())
                   .build()
                   .parseClaimsJws(token)
                   .getBody();
    }

     // Token geçerlilik kontrolü
    public boolean validateToken(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }


    

}