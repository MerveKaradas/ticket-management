package com.kafein.ticket_management.controller;

import org.springframework.web.bind.annotation.RestController;

import com.kafein.ticket_management.dto.response.ResponseAdminDashboardSummaryDto;
import com.kafein.ticket_management.dto.response.ResponseDashboardSummaryDto;
import com.kafein.ticket_management.service.DashboardService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;


import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Tag(name = "Dashboard API", description = "Sistem Genelinde Ticket İstatistiklerini ve Uygulama Özet Bilgileri") 
@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @Operation(summary = "Genel Dashboard Özeti",
        description = "Sistemdeki ticketlara ait genel bilgileri döndürür. "
                    + "Bu endpoint tüm kullanıcılar tarafından görüntülenebilir. "
    )
    @GetMapping("/summary")
    public ResponseEntity<ResponseDashboardSummaryDto> getDashboardSummary() {
        return ResponseEntity.ok(dashboardService.getDashboardSummary());
    }


    @Operation(summary = "Admin Dashboard Özeti",
       description = "Sistem yöneticileri için detaylı istatistikleri döndürür. "
                    + "Bu endpoint sadece 'ADMIN' yetkisine sahip kullanıcılar tarafından erişilebilir. "
                    + "Kullanıcı sayısı, sistem hata oranı ve diğer yönetimsel metrikleri içerir.")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/summary")
    public ResponseEntity<ResponseAdminDashboardSummaryDto> getAdminSummary() {
        return ResponseEntity.ok(dashboardService.getAdminDashboardSummary());
    }
 
}
