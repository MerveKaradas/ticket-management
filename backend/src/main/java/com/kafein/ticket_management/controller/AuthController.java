package com.kafein.ticket_management.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kafein.ticket_management.dto.request.RequestLoginDto;
import com.kafein.ticket_management.dto.response.ResponseLoginDto;
import com.kafein.ticket_management.service.AuthService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

@Tag(name = "Auth API", description = "Login, Logout ve Refresh İşlemleri")
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @Operation(summary = "Sisteme Giriş", 
               description = "Sistemde kayıtlı bulunan kullanıcı sisteme giriş yaparak refresh ve access token alır ve access token ile sisteme giriş yapabilir.")
    @PostMapping("/login")
    public ResponseEntity<ResponseLoginDto> login(@RequestBody @Valid RequestLoginDto requestLoginDto,HttpServletResponse response, HttpServletRequest request) {
        return ResponseEntity.ok(authService.login(requestLoginDto,response,request));
    }

    @Operation(summary = "Mevcut Oturumu Sonlandırma", description = "Kullanıcının oturum açtığı cihaz oturumu sonlandırılır")
    @PostMapping("/logout") 
    public ResponseEntity<Void> logout(@CookieValue(name = "refreshToken", required = false) String refreshToken, HttpServletResponse response) {
        authService.logout(refreshToken, response);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Tüm Oturumları Sonlandırma", description = "Kullanıcının oturum açtığı tüm cihazlardaki oturumları sonlandırılır")
    @PostMapping("/logoutAll")
    public ResponseEntity<Void> logoutAll(HttpServletResponse response) {
        authService.logoutAll(response);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Token Yenileme", description = "Refresh token geçerli olduğu sürece yeni access token ve refresh token alınır")
    @PostMapping("/refresh") 
    public ResponseEntity<?> refresh(@CookieValue(name = "refreshToken") String oldRefreshToken, HttpServletResponse response) {
        return ResponseEntity.ok(authService.refresh(oldRefreshToken,response));
    }
    
    
}
