package com.kafein.ticket_management.mapper;

import org.mapstruct.Mapper;

import com.kafein.ticket_management.dto.response.ResponseCreateTicketDto;
import com.kafein.ticket_management.dto.response.ResponseGetAllTicketsDto;
import com.kafein.ticket_management.model.Ticket;

@Mapper(componentModel = "spring")
public interface TicketMapper {
    ResponseCreateTicketDto toDto(Ticket ticket);
    ResponseGetAllTicketsDto toGetAllTicketsDto(Ticket ticket);
}