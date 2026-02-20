package com.kafein.ticket_management.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kafein.ticket_management.dto.request.RequestCommentDto;
import com.kafein.ticket_management.dto.response.ResponseCommentDto;
import com.kafein.ticket_management.service.CommentService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/comments")
@PreAuthorize("hasRole('USER')")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }


    @PostMapping("/{ticketId}/comments")
    public ResponseEntity<ResponseCommentDto> addComment(@PathVariable UUID ticketId, @RequestBody @Valid RequestCommentDto commentDto){
        return ResponseEntity.ok(commentService.addComment(ticketId,commentDto));
    }

    @GetMapping("/{ticketId}")
    public ResponseEntity<List<ResponseCommentDto>> getAllCommentsByTicketId(@PathVariable UUID ticketId){
        return ResponseEntity.ok(commentService.getAllCommentsByTicketId(ticketId));

    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable UUID commentId){
        commentService.deleteComment(commentId);
        return ResponseEntity.ok().build();
    } 
    
    
}
