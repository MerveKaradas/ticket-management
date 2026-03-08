package com.kafein.ticket_management.service;

import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.kafein.ticket_management.aop.Audit;
import com.kafein.ticket_management.config.JwtProperties;
import com.kafein.ticket_management.dto.request.RequestLoginDto;
import com.kafein.ticket_management.dto.response.ResponseLoginDto;
import com.kafein.ticket_management.exception.UnauthorizedException;
import com.kafein.ticket_management.model.RefreshToken;
import com.kafein.ticket_management.model.User;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;

@Service
public class AuthService {

    private final TokenService tokenService;
    private final UserService userService;
    private final JwtProperties jwtProperties;
    private final PasswordEncoder passwordEncoder;

    public AuthService(TokenService tokenService, UserService userService, JwtProperties jwtProperties,
            PasswordEncoder passwordEncoder) {
        this.tokenService = tokenService;
        this.userService = userService;
        this.jwtProperties = jwtProperties;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    @Audit(action = "USER_LOGIN")
    public ResponseLoginDto login(RequestLoginDto requestLoginDto, HttpServletResponse response,
            HttpServletRequest request) {

        User user = userService.getUserByEmail(requestLoginDto.email())
                .filter(u -> passwordEncoder.matches(requestLoginDto.password(), u.getPassword()))
                .orElseThrow(() -> new BadCredentialsException("Email veya şifre hatalı"));

        Map<String, String> tokens = tokenService.generateToken(user);

        String userAgent = request.getHeader("User-Agent");

        tokenService.saveRefreshToken(tokens.get("refreshToken"), user, userAgent);

        ResponseCookie cookie = ResponseCookie.from("refreshToken", tokens.get("refreshToken"))
                .httpOnly(true) // JS erişemez (XSS koruması)
                .secure(true) // Sadece HTTPS üzerinden 
                .path("/") // Tüm uygulama yollarında geçerli
                .maxAge(jwtProperties.getRefreshTokenExpirationInMs()) 
                .sameSite("Lax") // CSRF koruması
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        return new ResponseLoginDto(tokens.get("accessToken"));

    }

    @Transactional
    public String refresh(String oldRefreshToken, HttpServletResponse response) {
        
        RefreshToken tokenEntity = tokenService.getRefreshTokenByToken(oldRefreshToken)
                .orElseThrow(() -> new SecurityException(
                        "Token bulunamadı veya daha önce kullanılmış"));

        User user = tokenEntity.getUser();

        tokenService.revokeRefreshToken(oldRefreshToken);

        Map<String, String> tokens = tokenService.generateToken(user);

        // Aynı cihaz bilgisini koruyarak kaydediyoruz
        tokenService.saveRefreshToken(tokens.get("refreshToken"), user, tokenEntity.getUserAgent());

        ResponseCookie cookie = ResponseCookie.from("refreshToken", tokens.get("refreshToken"))
                .httpOnly(true)
                .secure(true)
                .path("/").build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        return tokens.get("accessToken");

    }

    @Transactional
    public void logout(String currentRefreshToken, HttpServletResponse response) {

        if (currentRefreshToken != null && !currentRefreshToken.isBlank()) {
            tokenService.revokeRefreshToken(currentRefreshToken);
        }

        ResponseCookie cleanCookie = ResponseCookie.from("refreshToken", "")
                .httpOnly(true)
                .secure(true) 
                .path("/")
                .maxAge(0) // Hemen yok eder
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cleanCookie.toString());
    }

    @Transactional
    public void logoutAll(HttpServletResponse response) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth != null && auth.getPrincipal() instanceof User user) {
            tokenService.revokeAllRefreshToken(user);
        } else {
            throw new UnauthorizedException();
        }

        ResponseCookie cleanCookie = ResponseCookie.from("refreshToken", "")
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(0)
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cleanCookie.toString());
    }

}
