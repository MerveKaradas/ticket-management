package com.kafein.ticket_management.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import com.kafein.ticket_management.dto.request.RequestCommentDto;
import com.kafein.ticket_management.exception.BusinessException;
import com.kafein.ticket_management.exception.ResourceNotFoundException;
import com.kafein.ticket_management.mapper.CommentMapper;
import com.kafein.ticket_management.model.Comment;
import com.kafein.ticket_management.model.Ticket;
import com.kafein.ticket_management.model.User;
import com.kafein.ticket_management.repository.CommentRepository;

@ExtendWith(MockitoExtension.class)
public class CommentServiceTest {

    @Mock
    private CommentRepository commentRepository;
    @Mock
    private UserService userService;
    @Mock
    private TicketService ticketService;

    @Spy
    private CommentMapper commentMapper = Mappers.getMapper(CommentMapper.class);

    @InjectMocks
    private CommentService commentService;
    @Test
    void addComment_WhenTicketExists() {

        // ARRANGE
        UUID ticketId = UUID.randomUUID();
        Ticket ticket = Ticket.builder().id(ticketId).title("Ornek ticket").build();
        RequestCommentDto commentDto = new RequestCommentDto("Ornek yorum");
        Comment comment = new Comment();

        given(ticketService.getTicketById(ticketId)).willReturn(Optional.of(ticket));
        given(commentRepository.save(any(Comment.class))).willReturn(comment);
        // ACT
        commentService.addComment(ticketId, commentDto);

        // ASSERT
        verify(commentMapper, times(1)).toDto(any(Comment.class));

    }

    @Test
    void addComment_WhenTicketDoesnotExists_ResourceNotFoundException() {

        // ARRANGE
        UUID ticketId = UUID.randomUUID();

        given(ticketService.getTicketById(ticketId)).willReturn(Optional.empty());

        // ACT
        assertThrows(ResourceNotFoundException.class, () -> {
            commentService.addComment(ticketId, any(RequestCommentDto.class));
        });

        // ASSERT
        verifyNoInteractions(commentMapper);

    }

    @Test
    void getAllCommentsByTicketId() {
        // ARRANGE
        UUID ticketId = UUID.randomUUID();
        Ticket ticket = Ticket.builder().id(ticketId).title("Ornek ticket").build();
        Comment comment = Comment.builder().ticket(ticket).build();
        List<Comment> comments = List.of(comment, comment);

        given(ticketService.getTicketById(ticketId)).willReturn(Optional.of(ticket));
        given(commentRepository.findAllByTicket(ticket)).willReturn(comments);
        // ACT
        commentService.getAllCommentsByTicketId(ticketId);

        // ASSERT
        verify(commentMapper, times(2)).toDto(any(Comment.class));
    }

    @Test
    void deleteComment1() {
        // ARRANGE
        UUID commentId = UUID.randomUUID();

        given(commentRepository.findById(commentId)).willReturn(Optional.empty());

        // ACT ve ASSERT
        assertThrows(ResourceNotFoundException.class, () -> {
            commentService.deleteComment(commentId);
        });

    }

    @Test
    void deleteComment() {
        // ARRANGE
        UUID userId = UUID.randomUUID();
        User user = User.builder().id(userId).build();
        UUID commentId = UUID.randomUUID();
        Comment comment = Comment.builder().author(user).build();

        given(commentRepository.findById(commentId)).willReturn(Optional.of(comment));
        given(userService.getCurrentUser()).willReturn(user);
        // ACT
        commentService.deleteComment(commentId);

        // ASSERT
        verify(commentRepository, times(1)).deleteById(commentId);

    }

    @Test
    void deleteComment3() {
        // ARRANGE
        UUID userId = UUID.randomUUID();
        User user = User.builder().id(userId).build();
        User otherUser = User.builder().id(UUID.randomUUID()).build();
        UUID commentId = UUID.randomUUID();
        Comment comment = Comment.builder().author(user).build();

        given(commentRepository.findById(commentId)).willReturn(Optional.of(comment));
        given(userService.getCurrentUser()).willReturn(otherUser);
        // ACT
        assertThrows(BusinessException.class, () -> {
            commentService.deleteComment(commentId);
        });


    }

}
