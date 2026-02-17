package com.kafein.ticket_management.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kafein.ticket_management.dto.request.RequestCreateUserDto;
import com.kafein.ticket_management.dto.request.RequestLoginDto;
import com.kafein.ticket_management.dto.response.ResponseUserDto;
import com.kafein.ticket_management.service.UserService;


@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/createUser") // TODO : Validated ve valide tekrar bak
    public ResponseEntity<ResponseUserDto> createUser(@RequestBody @Validated RequestCreateUserDto requestCreateUserDto){
        return ResponseEntity.ok(userService.createUser(requestCreateUserDto));
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody RequestLoginDto requestLoginDto) {
        return ResponseEntity.ok(userService.login(requestLoginDto));
    }



    
    
}
