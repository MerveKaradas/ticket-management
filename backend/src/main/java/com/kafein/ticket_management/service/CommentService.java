package com.kafein.ticket_management.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.kafein.ticket_management.dto.request.RequestCommentDto;
import com.kafein.ticket_management.dto.response.ResponseCommentDto;
import com.kafein.ticket_management.exception.BusinessException;
import com.kafein.ticket_management.exception.ResourceNotFoundException;
import com.kafein.ticket_management.mapper.CommentMapper;
import com.kafein.ticket_management.model.Comment;
import com.kafein.ticket_management.model.Ticket;
import com.kafein.ticket_management.repository.CommentRepository;

import jakarta.transaction.Transactional;

@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final UserService userService;
    private final TicketService ticketService;
    private final CommentMapper commentMapper;

    public CommentService(CommentRepository commentRepository, TicketService ticketService, CommentMapper commentMapper, UserService userService) {
        this.commentRepository = commentRepository;
        this.userService = userService;
        this.ticketService = ticketService;
        this.commentMapper = commentMapper;
    }

    @Transactional
    public ResponseCommentDto addComment(UUID ticketId, RequestCommentDto commentDto) {

        Ticket ticket = ticketService.getTicketById(ticketId)
                        .orElseThrow(() -> new ResourceNotFoundException("Ticket", "ticketId", ticketId));

        Comment comment = new Comment();
        comment.setContent(commentDto.content());
        comment.setTicket(ticket);
        Comment newComment = commentRepository.save(comment);
        
        return commentMapper.toDto(newComment);
        
    }

    public List<ResponseCommentDto> getAllCommentsByTicketId(UUID ticketId) {
        Ticket ticket = ticketService.getTicketById(ticketId)
                        .orElseThrow(() -> new ResourceNotFoundException("Ticket", "ticketId", ticketId));

        List<Comment> comments = commentRepository.findAllByTicketOrderByCreatedAtDesc(ticket);
        return commentMapper.toDtoList(comments);
    }


    @Transactional
    public void deleteComment(UUID commentId) {

        Comment comment = commentRepository.findById(commentId)
                            .orElseThrow(()-> new ResourceNotFoundException("Comment", "commentId", commentId));
        
        if(!userService.getCurrentUser().getId().equals(comment.getAuthor().getId())){
            throw new BusinessException("Bu yorumu sadece oluşturan kullanıcı silebilir!");
        }
        commentRepository.deleteById(commentId);
    }

    
    
}
