package com.kafein.ticket_management.mapper;

import org.mapstruct.Mapper;

import com.kafein.ticket_management.dto.response.ResponseUserDto;
import com.kafein.ticket_management.model.User;

@Mapper(componentModel = "spring")
public interface UserMapper {

    ResponseUserDto toDto(User user);

}
