package com.kafein.ticket_management.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.kafein.ticket_management.aop.Audit;
import com.kafein.ticket_management.model.RefreshToken;
import com.kafein.ticket_management.model.User;
import com.kafein.ticket_management.repository.TokenRepository;
import com.kafein.ticket_management.security.JwtUtil;

import jakarta.transaction.Transactional;

@Service
public class TokenService {

    private final TokenRepository tokenRepository;
    private final JwtUtil jwtUtil;

    public TokenService(TokenRepository tokenRepository, JwtUtil jwtUtil) {
        this.tokenRepository = tokenRepository;
        this.jwtUtil = jwtUtil;
    }

    @Transactional
    public void saveRefreshToken(String refreshTokenString, User user, String userAgent) {

        RefreshToken refreshToken = RefreshToken.builder()
                .token(refreshTokenString)
                .user(user)
                .userAgent(userAgent)
                .build();

        tokenRepository.save(refreshToken);

    }

    @Transactional
    public void revokeAllRefreshToken(User user) { // Tüm mevcut tokenler siliniyor(tüm cihazlar için)
        List<RefreshToken> refreshTokens = tokenRepository.findByUserId(user.getId());
        if (!refreshTokens.isEmpty()) { // Sadece liste doluysa işlem yap
            refreshTokens.forEach(token -> {
                token.setRevoked(true);
                tokenRepository.delete(token);
            });
        }
    }

    @Transactional
    public void revokeRefreshToken(String currentRefreshToken) {

        tokenRepository.findByToken(currentRefreshToken).ifPresent(token -> {

            tokenRepository.delete(token);
        });
    }

    public Optional<RefreshToken> getRefreshTokenByToken(String token){
        return tokenRepository.findByToken(token);
    }
    

    public Map<String,String> generateToken(User user){

        Map<String,String> tokens = new HashMap<>();

        String newAccessToken = jwtUtil.generateToken(user);
        String newRefreshToken = jwtUtil.generateRefreshToken(user);

        tokens.put("accessToken", newAccessToken);
        tokens.put("refreshToken", newRefreshToken);
        return tokens;

    }

    @Transactional
    @Audit(action = "REVOKE_ALL_TOKEN")
    public void revokeAllTokens() {
        List<RefreshToken> allTokens = tokenRepository.findAll();
        allTokens.forEach((token) -> token.setRevoked(true));
        tokenRepository.saveAll(allTokens);
    } 

    @Scheduled(cron = "0 0 3 * * ?") 
    @Transactional
    public void purgeRevokedTokens() {
        // admin tarafından iptal edilmiş olanları siler
        tokenRepository.deleteByRevokedTrue();
        //TODO : süresi geçenler için de uygulanacak
        
        
    }
}
