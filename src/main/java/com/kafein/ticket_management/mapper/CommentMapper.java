package com.kafein.ticket_management.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.kafein.ticket_management.dto.response.ResponseCommentDto;
import com.kafein.ticket_management.model.Comment;

@Mapper(componentModel = "spring")
public interface CommentMapper {

    @Mapping(source = "ticket.id", target = "ticketId")
    @Mapping(source = "author.id", target = "authorId")
    ResponseCommentDto toDto(Comment comment);

    List<ResponseCommentDto> toDtoList(List<Comment> comments);
    
}
