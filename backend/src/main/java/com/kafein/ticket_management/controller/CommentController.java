package com.kafein.ticket_management.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Tag(name = "Comment API", description = "Biletlere Yorum Ekleme,Silme ve Listeleme İşlemleri")
@RestController
@RequestMapping("/api/comments")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @Operation(summary = "Yeni Yorum Ekleme")
    @PostMapping("/{ticketId}/comment")
    public ResponseEntity<ResponseCommentDto> addComment(@PathVariable UUID ticketId,
            @RequestBody @Valid RequestCommentDto commentDto) {
        return ResponseEntity.ok(commentService.addComment(ticketId, commentDto));
    }

    @Operation(summary = "Yorumları Görüntüleme", 
                description = "Bilet altında bulunan tüm yorumları görüntülenebilir.")
    @GetMapping("/{ticketId}")
    public ResponseEntity<List<ResponseCommentDto>> getAllCommentsByTicketId(@PathVariable UUID ticketId) {
        return ResponseEntity.ok(commentService.getAllCommentsByTicketId(ticketId));

    }

    @Operation(summary = "Yorum Silme", description = "Sadece yorumu oluşturan kullanıcı tarafından yorum silinebilir.")
    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable UUID commentId) {
        commentService.deleteComment(commentId);
        return ResponseEntity.ok().build();
    }

}
