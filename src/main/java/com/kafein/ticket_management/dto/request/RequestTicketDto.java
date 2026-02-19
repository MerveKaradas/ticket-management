package com.kafein.ticket_management.dto.request;

import java.util.UUID;

import com.kafein.ticket_management.model.enums.TicketPriority;
import com.kafein.ticket_management.model.enums.TicketStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RequestTicketDto {

    private String title;
    private String description;
    private TicketStatus status;
    private TicketPriority priority;
    private UUID assignedToId;

}
