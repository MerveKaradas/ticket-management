package com.kafein.ticket_management.service;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.kafein.ticket_management.model.RefreshToken;
import com.kafein.ticket_management.model.User;
import com.kafein.ticket_management.repository.RefreshTokenRepository;

@Service
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
    }

    public void saveRefreshToken(String refreshTokenString, UUID userId) {

        RefreshToken refreshToken = RefreshToken.builder()
                .token(refreshTokenString)
                .userId(userId)
                .build();

        refreshTokenRepository.save(refreshToken);
    }

    public void revokeRefreshToken(User user) {
        
        RefreshToken refreshToken = refreshTokenRepository.findByUserId(user.getId())
                                .orElseThrow(()-> new RuntimeException("Refresh token bulunamadi"));

        refreshTokenRepository.delete(refreshToken);
    }
}
