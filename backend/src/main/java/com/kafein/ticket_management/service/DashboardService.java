package com.kafein.ticket_management.service;

import java.util.List;
import java.util.Map;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kafein.ticket_management.dto.response.ResponseAdminDashboardSummaryDto;
import com.kafein.ticket_management.dto.response.ResponseAuditLogDto;
import com.kafein.ticket_management.dto.response.ResponseDashboardSummaryDto;

@Service
public class DashboardService {

    private final TicketAnalyticsService ticketAnalyticsService;
    private final AdminService adminService;
    private final UserService userService;

  
    public DashboardService(TicketAnalyticsService ticketAnalyticsService, AdminService adminService,
            UserService userService) {
        this.ticketAnalyticsService = ticketAnalyticsService;
        this.adminService = adminService;
        this.userService = userService;
    }

    @Transactional(readOnly = true)
    public ResponseDashboardSummaryDto getDashboardSummary() {
        return ResponseDashboardSummaryDto.builder()
                .totalTicketCount(ticketAnalyticsService.totalTicketCount())
                .eachStatusTotalTicketsCount(ticketAnalyticsService.getEachStatusTotalTicketsCount())
                .totalPriority(ticketAnalyticsService.getTotalPriority())
                .last5Tickets(ticketAnalyticsService.getLast5Tickets())
                .build();
    }

    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseAdminDashboardSummaryDto getAdminDashboardSummary() {

        Long totalUserCount = userService.totalUserCount();
        Long activeSessions = 0L;

        Double failRate = adminService.calculateSystemFailRate();
        Double averageResolveTime = ticketAnalyticsService.calculateAverageResolveTime();
        Map<String, Long> userWorkloadDistribution = ticketAnalyticsService.getUserWorkloadDistribution();
        Map<String, Long> getDailyTrendAnalysis = ticketAnalyticsService.getDailyTrendAnalysis();
        List<ResponseAuditLogDto> getRecentSecurityLogs = adminService.getRecentSecurityLogs();

        return ResponseAdminDashboardSummaryDto.builder()
        .totalUsers(totalUserCount)
        .activeSessions(activeSessions) // TODO : Düzenlencek
        .averageResolveTime(averageResolveTime)
        .failRate(failRate)
        .userWorkloadDistribution(userWorkloadDistribution)
        .dailyTrendAnalysis(getDailyTrendAnalysis)
        .recentSecurityActivities(getRecentSecurityLogs)
        .build();



    }

}
