package com.kafein.ticket_management.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.kafein.ticket_management.model.enums.Role;


@Configuration
@EnableWebSecurity // güvenlik yapilandirmasi etkinleştirir
@EnableMethodSecurity // method seviyesinde güvenlik kontrolleri için gerekli anatasyonları etkinleştirir
public class SecurityConfig {
    
    private final JwtAuthFilter jwtAuthFilter;

    
    public SecurityConfig(JwtAuthFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
    }


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/user/login", "/api/user/createUser").permitAll()
                        .requestMatchers("/swagger.html","/swagger-ui/**", "/swagger-ui.html", "/docs/**", "/v3/api-docs/**").permitAll()
                        .requestMatchers("/api/admin/**").hasRole(Role.ADMIN.name())
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    
}
