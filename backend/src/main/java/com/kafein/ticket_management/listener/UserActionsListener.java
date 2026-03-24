package com.kafein.ticket_management.listener;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.kafein.ticket_management.event.user.UserDeletedEvent;
import com.kafein.ticket_management.service.TicketService;

@Component
public class UserActionsListener {

    private final TicketService ticketService;

    public UserActionsListener(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @EventListener
    public void onUserDeleted(UserDeletedEvent event) {
        ticketService.updateAllTicketsByDeletedUserId(event.getUserId());

    }


    
}
