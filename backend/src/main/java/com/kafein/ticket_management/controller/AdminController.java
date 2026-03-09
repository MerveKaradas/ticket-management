package com.kafein.ticket_management.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kafein.ticket_management.model.AuditLog;
import com.kafein.ticket_management.service.AdminService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Admin API", description = "Admin Yönetim İşlemleri")
@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @Operation(summary = "Audit Logları Görüntüleme", description = "'ADMIN' yetkisine sahip kullanıcı tarafından sistemdeki audit loglar görüntülenir ")
    @GetMapping("/logs")
    public ResponseEntity<Page<AuditLog>> getAuditLogs(
            @PageableDefault(
                size = 10,
                page = 0, 
                sort = "createdAtDate", 
                direction = Sort.Direction.DESC)
            Pageable pageable) {
        return ResponseEntity.ok(adminService.findAll(pageable));
    }

    @Operation(summary = "Sistemdeki Kayıtlı Tüm Refresh Tokenları İptal Etme", description = "'ADMIN' yetkisine sahip kullanıcı tarafından şüpheli durumlarda sistemdeki tüm refresh tokenlar iptal edilir")
    @PostMapping("/logout-all")
    public ResponseEntity<Void> logoutAllUsers() {
        adminService.revokeAllTokens();
        return ResponseEntity.ok().build();
    }

}
