package com.kafein.ticket_management.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.kafein.ticket_management.model.RefreshToken;
import com.kafein.ticket_management.model.User;
import com.kafein.ticket_management.repository.RefreshTokenRepository;

@ExtendWith(MockitoExtension.class)
public class RefreshTokenTest {

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @InjectMocks
    private RefreshTokenService refreshTokenService;

    @Test
    void saveRefreshToken(){

        UUID userId =  UUID.randomUUID();
        String refreshTokenString = "myRefreshToken";
        refreshTokenService.saveRefreshToken(refreshTokenString,userId);

        verify(refreshTokenRepository, times(1)).save(any(RefreshToken.class));

    }

    @Test
    void revokeRefreshToken(){
        UUID userId = UUID.randomUUID();
        User user = User.builder().id(userId).build(); 
        RefreshToken refreshToken = new RefreshToken();

        given(refreshTokenRepository.findByUserId(userId)).willReturn(Optional.of(refreshToken));
 
        // ACT
        refreshTokenService.revokeRefreshToken(user);

        // ASSERT
        verify(refreshTokenRepository, times(1)).delete(refreshToken);

    }

    @Test
    void revokeRefreshToken1(){
        UUID userId = UUID.randomUUID();
        User user = User.builder().id(UUID.randomUUID()).build(); 
        RefreshToken refreshToken = new RefreshToken();

        given(refreshTokenRepository.findByUserId(userId)).willReturn(Optional.empty());
 
        // ACT
        assertThrows(RuntimeException.class, ()->{
            refreshTokenService.revokeRefreshToken(user);
        });
        
        // ASSERT
        verify(refreshTokenRepository, never()).delete(refreshToken);

    }
    
}
