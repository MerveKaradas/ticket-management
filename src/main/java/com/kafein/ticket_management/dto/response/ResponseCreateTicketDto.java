package com.kafein.ticket_management.dto.response;

import java.time.LocalDateTime;
import com.kafein.ticket_management.model.User;
import com.kafein.ticket_management.model.enums.TicketPriority;
import com.kafein.ticket_management.model.enums.TicketStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Setter;

// @Setter
// @NoArgsConstructor
// @AllArgsConstructor
// @Builder
// public class ResponseCreateTicketDto {

//     private String title;
//     private String description;
//     private TicketStatus status;
//     private TicketPriority priority;
//     private User createdBy;
//     private User assignedTo;
//     private LocalDateTime createdAtDate;
//     private LocalDateTime updatedDate;

// }

@Builder
public record ResponseCreateTicketDto(
    String title,
    String description,
    TicketStatus status,
    TicketPriority priority,
    User createdBy,
    User assignedTo,
    LocalDateTime createdAtDate,
    LocalDateTime updatedDate) {

}
