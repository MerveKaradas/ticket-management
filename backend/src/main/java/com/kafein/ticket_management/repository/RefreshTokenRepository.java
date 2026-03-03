package com.kafein.ticket_management.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kafein.ticket_management.model.RefreshToken;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    boolean existsByUserId(UUID userId);

    Optional<RefreshToken> findByToken(String token);

    List<RefreshToken> findByUserId(UUID id);

    
}
