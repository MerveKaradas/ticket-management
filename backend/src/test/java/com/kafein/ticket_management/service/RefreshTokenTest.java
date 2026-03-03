package com.kafein.ticket_management.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.List;
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
    void saveRefreshToken_validRequest_shouldSaveToDatabase(){

        UUID userId =  UUID.randomUUID();
        String refreshTokenString = "myRefreshToken";
        refreshTokenService.saveRefreshToken(refreshTokenString,userId);

        verify(refreshTokenRepository, times(1)).save(any(RefreshToken.class));

    }

    @Test
    void revokeRefreshToken_tokenExists_shouldDeleteTokenFromDatabase(){
        String currentRefreshToken = "My_refresh_token";
        RefreshToken refreshToken = new RefreshToken();
      
        given(refreshTokenRepository.findByToken(currentRefreshToken)).willReturn(Optional.of(refreshToken));
 
        // ACT
        refreshTokenService.revokeRefreshToken(currentRefreshToken);

        // ASSERT
        verify(refreshTokenRepository, times(1)).delete(refreshToken);

    }

    @Test
    void revokeRefreshToken_tokenDoesNotExist_shouldNotAttemptDeletion(){
        String currentRefreshToken = "My_refresh_token";
        given(refreshTokenRepository.findByToken(currentRefreshToken)).willReturn(Optional.empty());
 
        // ACT
        refreshTokenService.revokeRefreshToken(currentRefreshToken);
        
        // ASSERT
        verify(refreshTokenRepository, never()).delete(any(RefreshToken.class));

    }

    @Test
    void revokeAllRefreshToken_userHasNoTokens_shouldNotPerformAnyDeletion(){
        // ARRANGE
        User user = User.builder().id(UUID.randomUUID()).build();
        List<RefreshToken> list = List.of();

        given(refreshTokenRepository.findByUserId(user.getId())).willReturn(list);

         // ACT
        refreshTokenService.revokeAllRefreshToken(user);
        
        // ASSERT
        verify(refreshTokenRepository, never()).delete(any(RefreshToken.class));

    }

    @Test
    void revokeAllRefreshToken_userHasMultipleTokens_shouldDeleteAllUserTokens(){
        // ARRANGE
        User user = User.builder().id(UUID.randomUUID()).build();
        List<RefreshToken> list = List.of(RefreshToken.builder().id(1001L).build());

        given(refreshTokenRepository.findByUserId(user.getId())).willReturn(list);

         // ACT
        refreshTokenService.revokeAllRefreshToken(user);
        
        // ASSERT
        verify(refreshTokenRepository, times(list.size())).delete(any(RefreshToken.class));

    }
    
}
