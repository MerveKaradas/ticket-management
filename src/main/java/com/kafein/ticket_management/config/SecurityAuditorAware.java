package com.kafein.ticket_management.config;

import java.util.Optional;

import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.kafein.ticket_management.model.User;

public class SecurityAuditorAware implements AuditorAware<User>{

    @Override
    public Optional<User> getCurrentAuditor() {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        // Kullanıcı giriş yapmamışsa veya anonimse 
        if ( auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            return Optional.empty();
        }

        return Optional.of((User) auth.getPrincipal());
        
    }
    
}
