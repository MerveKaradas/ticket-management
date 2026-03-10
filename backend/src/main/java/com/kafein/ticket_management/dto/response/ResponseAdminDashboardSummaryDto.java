package com.kafein.ticket_management.dto.response;

import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class ResponseAdminDashboardSummaryDto {
   
    private final Long totalUsers;
    private final Long activeSessions;
    private final Double averageResolveTime;
    private final Double failRate;

    private final Map<String, Long> userWorkloadDistribution;
    private final Map<String, Long> dailyTrendAnalysis;
    private final List<ResponseAuditLogDto> recentSecurityActivities;
}