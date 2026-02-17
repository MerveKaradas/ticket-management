package com.kafein.ticket_management.dto.request;

import java.util.UUID;

import com.kafein.ticket_management.model.enums.TicketPriority;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RequestCreateTicketDto {

    private String title;
    private String description;
    private TicketPriority priority;
    private UUID assignedToId;
    
}
