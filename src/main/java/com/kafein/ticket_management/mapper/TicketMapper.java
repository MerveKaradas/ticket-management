package com.kafein.ticket_management.mapper;

import org.mapstruct.Mapper;

import com.kafein.ticket_management.dto.response.ResponseCreateTicketDto;
import com.kafein.ticket_management.dto.response.ResponseTicketDto;
import com.kafein.ticket_management.model.Ticket;

@Mapper(componentModel = "spring")
public interface TicketMapper {
    ResponseCreateTicketDto toCreateTicketDto(Ticket ticket);

    ResponseTicketDto toDto(Ticket ticket);

    // @Mapping(target = "id", ignore = true)
    // @Mapping(target = "status", ignore = true) // Statü genel güncelleme ile değişmesin
    // @Mapping(target = "assignedTo", ignore = true) // Atanan kişi genel güncelleme ile değişmesin
    // void updateEntityFromDto(RequestTicketDto dto, @MappingTarget Ticket entity);
}