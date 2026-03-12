package com.kafein.ticket_management.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kafein.ticket_management.dto.request.RequestCreateUserDto;
import com.kafein.ticket_management.dto.response.ResponseUserDto;
import com.kafein.ticket_management.dto.response.ResponseUserForAssignmentDto;
import com.kafein.ticket_management.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Tag(name = "User API", description = "User Oluşturma, Silme ve Listeleme İşlemleri")
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

    @Operation(summary = "Tüm Kullanıcıları Listeleme", 
               description = "Sadece 'ADMIN' yetkisine sahip olan kullanıcılar sistemde kayıtlı olan tüm kullanıcı listesini görünteyebilir.")
    @GetMapping("/getAllUsers")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<ResponseUserDto>> getAllUsers(
        @RequestParam(required = false) String query,
        @PageableDefault(
                size = 10,
                page = 0, 
                sort = "createdAt", 
                direction = Sort.Direction.DESC)
            Pageable pageable){
        return ResponseEntity.ok(userService.getAllUsers(query, pageable));
    }

    @Operation(summary = "Kullanıcı Silme", 
                description = "Sadece 'ADMIN' yetkisine sahip olan kullanıcı belirli bir kullanıcıyı silebilir.")
    @DeleteMapping("/deleteUser/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteUserById(@PathVariable UUID id){
        userService.deleteUserById(id);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Kullanıcı Bilgisi", 
               description = "Sistemdeki mevcut kullanıcı, kendi bilgilerini görünteyebilir.")
    @GetMapping("/getCurrentUser")
    public ResponseEntity<ResponseUserDto> getCurrentUser(){
        return ResponseEntity.ok(userService.getUser());
    }

    @Operation(summary = "Tüm Kullanıcıları İsimleri ve ID Değerleri ile Listeleme", 
               description = "Frontend kullanımında bilet atama işlemleri için gerekli olan bilgileri listeler.")
    @GetMapping("/listForAssignment")
    public ResponseEntity<List<ResponseUserForAssignmentDto>> getAllUsersForAssignment(){
        return ResponseEntity.ok(userService.getAllUsersForAssignment());

    }

}
