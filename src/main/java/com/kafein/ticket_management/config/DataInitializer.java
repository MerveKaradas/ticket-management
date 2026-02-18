package com.kafein.ticket_management.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.kafein.ticket_management.service.UserService;

import lombok.RequiredArgsConstructor;


@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserService userService;

    @Override
    public void run(String... args) throws Exception {
        userService.createAdminUser();
        
    }
    
}
