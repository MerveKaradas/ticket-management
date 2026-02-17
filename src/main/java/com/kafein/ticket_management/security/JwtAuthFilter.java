package com.kafein.ticket_management.security;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;


    public JwtAuthFilter(JwtUtil jwtUtil, UserDetailsService userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response); // Token yok veya yanlış formatta, diğer işlemlere devam etmiyoruz
                                                     // isteği sonraki katmana gönderiyoruz
            return;
        }

        String token = authHeader.substring(7);

        try {
            //SecurityContext içinde zaten bir kullanıcı varsa tekrar kontrol etmeye gerek yok
            if (SecurityContextHolder.getContext().getAuthentication() == null) {

                if (jwtUtil.validateToken(token)) {

                    UserDetails userDetails = userDetailsService
                            .loadUserByUsername(jwtUtil.getUserEmailFromToken(token));

                    var authToken = new UsernamePasswordAuthenticationToken(
                        userDetails, 
                        null,
                        userDetails.getAuthorities());

                    //istek detayları
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        } catch (Exception e) {
            log.error("Token validasyon hatası : {}", e.getMessage());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("Geçersiz veya süresi dolmuş token.");
            return;

        }

        filterChain.doFilter(request, response);

    }

}
