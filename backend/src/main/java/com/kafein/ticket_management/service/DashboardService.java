package com.kafein.ticket_management.service;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kafein.ticket_management.dto.response.ResponseAdminDashboardSummaryDto;
import com.kafein.ticket_management.dto.response.ResponseAuditLogDto;
import com.kafein.ticket_management.dto.response.ResponseDashboardSummaryDto;

@Service
public class DashboardService {

    private final TicketService ticketService;
    private final AdminService adminService;
    private final UserService userService;

    public DashboardService(TicketService ticketService, AdminService adminService, UserService userService) {
        this.ticketService = ticketService;
        this.adminService = adminService;
        this.userService = userService;
    }

    @Transactional(readOnly = true)
    public ResponseDashboardSummaryDto getDashboardSummary() {
        return ResponseDashboardSummaryDto.builder()
                .totalTicketCount(ticketService.totalTicketCount())
                .eachStatusTotalTicketsCount(ticketService.getEachStatusTotalTicketsCount())
                .totalPriority(ticketService.getTotalPriority())
                .last5Tickets(ticketService.getLast5Tickets())
                .build();
    }

    @Transactional(readOnly = true)
    public ResponseAdminDashboardSummaryDto getAdminDashboardSummary() {

        Long totalUserCount = userService.totalUserCount();
        Long activeSessions = 0L;

        Double failRate = adminService.calculateSystemFailRate();
        Double averageResolveTime = ticketService.calculateAverageResolveTime();
        Map<String, Long> userWorkloadDistribution = ticketService.getUserWorkloadDistribution();
        Map<String, Long> getDailyTrendAnalysis = ticketService.getDailyTrendAnalysis();
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
