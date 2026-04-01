package com.kafein.ticket_management.dto.response;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.kafein.ticket_management.config.EnumKeySerializer;
import com.kafein.ticket_management.model.enums.TicketPriority;
import com.kafein.ticket_management.model.enums.TicketStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ResponseDashboardSummaryDto {
    private Long totalTicketCount;

    @JsonSerialize(keyUsing = EnumKeySerializer.class)
    private Map<TicketStatus, Long> eachStatusTotalTicketsCount;

    @JsonSerialize(keyUsing = EnumKeySerializer.class)
    private Map<TicketPriority, Long> totalPriority;

    private List<ResponseTicketDto> last5Tickets;

}
