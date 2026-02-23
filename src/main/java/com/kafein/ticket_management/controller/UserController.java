package com.kafein.ticket_management.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kafein.ticket_management.dto.request.RequestCreateUserDto;
import com.kafein.ticket_management.dto.request.RequestLoginDto;
import com.kafein.ticket_management.dto.response.ResponseUserDto;
import com.kafein.ticket_management.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Tag(name = "User API", description = "User register, login ve listeleme işlemleri")
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(summary = "Kullanıcı Oluşturma", 
               description = "Sistemde daha önce kaydı bulunmayan kullanıcılar yeni kullanıcı kaydı oluşturabilir.")
    @PostMapping("/createUser") 
    public ResponseEntity<ResponseUserDto> createUser(@RequestBody @Valid RequestCreateUserDto requestCreateUserDto){
        return ResponseEntity.ok(userService.createUser(requestCreateUserDto));
    }

    @Operation(summary = "Sisteme Giriş", 
               description = "Sistemde kayıtlı bulunan kullanıcı sisteme giriş yaparak refresh ve access token alır ve access token ile sisteme giriş yapabilir.")
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody @Valid RequestLoginDto requestLoginDto) {
        return ResponseEntity.ok(userService.login(requestLoginDto));
    }

    @Operation(summary = "Tüm Kullanıcıları Listeleme", 
               description = "Sadece 'ADMIN' yetkisine sahip olan kullanıcılar sistemde kayıtlı olan tüm kullanıcı listesini görünteyebilir.")
    @GetMapping("/getAllUsers")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ResponseUserDto>> getAllUsers(){
        return ResponseEntity.ok(userService.getAllUsers());
    }
}
