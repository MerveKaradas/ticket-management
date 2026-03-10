package com.kafein.ticket_management.dto.response;

import java.util.List;
import java.util.Map;

import com.kafein.ticket_management.model.enums.TicketPriority;
import com.kafein.ticket_management.model.enums.TicketStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class ResponseDashboardSummaryDto {
    private Long totalTicketCount;
    private Map<TicketStatus, Long> eachStatusTotalTicketsCount;
    private Map<TicketPriority, Long> totalPriority;
    private List<ResponseTicketDto> last5Tickets;
    
}
