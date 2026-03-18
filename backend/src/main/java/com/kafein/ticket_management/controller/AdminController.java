package com.kafein.ticket_management.controller;

import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kafein.ticket_management.dto.response.ResponseReportDto;
import com.kafein.ticket_management.model.AuditLog;
import com.kafein.ticket_management.service.AdminService;
import com.kafein.ticket_management.service.ReportService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Admin API", description = "Admin Yönetim İşlemleri")
@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminService adminService;
    private final ReportService reportService;

    public AdminController(AdminService adminService, ReportService reportService) {
        this.adminService = adminService;
        this.reportService = reportService;
    }

    @Operation(summary = "Audit Logları Görüntüleme", description = "Sadece 'ADMIN' yetkisine sahip kullanıcı tarafından sistemdeki audit loglar görüntülenebilir.")
    @GetMapping("/logs")
    public ResponseEntity<Page<AuditLog>> getAuditLogs(
            @RequestParam(required = false) String query,
            @PageableDefault(size = 10, page = 0, sort = "createdAtDate", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(adminService.findAll(query, pageable));
    }

    @Operation(summary = "Sistemdeki Kayıtlı Tüm Refresh Tokenları İptal Etme", description = "Sadece 'ADMIN' yetkisine sahip kullanıcı tarafından şüpheli durumlarda sistemdeki tüm refresh tokenlar iptal edilir.")
    @PostMapping("/logout-all")
    public ResponseEntity<Void> logoutAllUsers() {
        adminService.revokeAllTokens();
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Audit Logları Dışa Aktarma", description = "Sadece 'ADMIN' yetkisine sahip kullanıcı tarafından audit loglar excel formatında dışa aktarılır.")
    @GetMapping("/export")
    public ResponseEntity<InputStreamResource> exportExcel(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) String format) {

        ResponseReportDto report = reportService.generateAuditLogReport(query, format);
        
        InputStreamResource resource = new InputStreamResource(report.getRawData());

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + report.getFileName() + "\"")
                .contentType(MediaType.parseMediaType(report.getContentType()))
                .contentLength(report.getRawData().available()) // Boyutu bildirerek bozulmayı önlüyoruz
                .body(resource);
    }

}
