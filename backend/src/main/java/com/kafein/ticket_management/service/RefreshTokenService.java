package com.kafein.ticket_management.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.kafein.ticket_management.model.RefreshToken;
import com.kafein.ticket_management.model.User;
import com.kafein.ticket_management.repository.RefreshTokenRepository;

import jakarta.transaction.Transactional;

@Service
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
    }

    @Transactional
    public void saveRefreshToken(String refreshTokenString, UUID userId) {

        RefreshToken refreshToken = RefreshToken.builder()
                .token(refreshTokenString)
                .userId(userId)
                .build();

        refreshTokenRepository.save(refreshToken);

    }

    @Transactional
    public void revokeAllRefreshToken(User user) { // Tüm mevcut tokenler siliniyor(tüm cihazlar için)
        List<RefreshToken> refreshTokens = refreshTokenRepository.findByUserId(user.getId());
        if (!refreshTokens.isEmpty()) { // Sadece liste doluysa işlem yap
            refreshTokens.forEach(token -> {
                token.setRevoked(true);
                refreshTokenRepository.delete(token);
            });
        }
    }

    @Transactional
    public void revokeRefreshToken(String currentRefreshToken) {

        refreshTokenRepository.findByToken(currentRefreshToken).ifPresent(token -> {
            token.setRevoked(true);
            refreshTokenRepository.delete(token);
        });
    }
}
