package com.kafein.ticket_management.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.test.context.support.WithMockUser;

import com.kafein.ticket_management.mapper.TicketMapper;
import com.kafein.ticket_management.model.Ticket;
import com.kafein.ticket_management.model.User;
import com.kafein.ticket_management.repository.TicketRepository;

import static com.kafein.ticket_management.util.TestDataFactory.createTestTicket;
import static com.kafein.ticket_management.util.TestDataFactory.createTestUser;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.BDDMockito.given;
import static org.mockito.ArgumentMatchers.any;

@SpringBootTest(classes = TicketService.class) 
@EnableMethodSecurity 
public class TicketServiceSecurityTest {

    @MockBean
    private TicketRepository ticketRepository;

    @MockBean
    private TicketMapper ticketMapper;

    @MockBean
    private UserService userService;

    @Autowired
    private TicketService ticketService;

    @Test
    @WithMockUser(roles = "USER")
    void deleteAllTickets_AsUser_ShouldThrowAccessDenied() {

        // ACT ve ASSERT
        assertThatThrownBy(() -> ticketService.deleteAllTickets())
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteAllTickets_AsAdmin_ShouldSucceed() {
        // ACT
        ticketService.deleteAllTickets();

        // ASSERT
        verify(ticketRepository, times(1)).deleteAll();
        verifyNoMoreInteractions(ticketRepository);

    }

    @Test
    @WithMockUser(roles = "USER")
    void getAllTickets_AsUser_ShouldThrowAccessDenied() {

        // ACT ve ASSERT
        assertThatThrownBy(() -> ticketService.getAllTickets())
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAllTickets_AsAdmin_ShouldSucceed() {

        // ACT
        var result = ticketService.getAllTickets();

        // ASSERT
        verify(ticketRepository, times(1)).findAll();
        verify(ticketMapper, times(result.size())).toDto(any(Ticket.class));
        verifyNoMoreInteractions(ticketRepository);

    }

    @Test
    @WithMockUser(roles = "USER")
    void deleteTicketById_AsUser_ShouldThrowAccessDenied() {

        // ARRANGE
        User user = createTestUser();
        Ticket ticket = createTestTicket(user);

        // ACT ve ASSERT
        assertThatThrownBy(() -> ticketService.deleteTicketById(ticket.getId()))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteTicketById_AsAdmin_ShouldSucceed() {

        // ARRANGE
        User user = createTestUser();
        Ticket ticket = createTestTicket(user);

        given(ticketRepository.existsById(ticket.getId())).willReturn(true);

        // ACT
        ticketService.deleteTicketById(ticket.getId());

        // ASSERT
        verify(ticketRepository, times(1)).deleteById(ticket.getId());
        verify(ticketRepository, times(1)).existsById(ticket.getId());
        verifyNoMoreInteractions(ticketRepository);

    }
}
