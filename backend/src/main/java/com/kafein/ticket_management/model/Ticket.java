package com.kafein.ticket_management.model;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.kafein.ticket_management.model.enums.TicketPriority;
import com.kafein.ticket_management.model.enums.TicketStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "tickets")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class) // değişiklilikleri dinlemek için
public class Ticket {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String description;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    @Column(nullable = false)
    private TicketStatus status = TicketStatus.OPEN;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TicketPriority priority;

    @CreatedBy // ilk atamada enjekte ediyor
    @ManyToOne
    @JoinColumn(name = "created_by_id", nullable = false, updatable = false)
    private User createdBy;
    
    @ManyToOne
    @JoinColumn(name = "assigned_to_id", nullable = false)
    private User assignedTo;

    @LastModifiedBy // Her güncellendiğinde kullanıcıyı otomatik günceller
    @ManyToOne
    @JoinColumn(name = "updated_by_id")
    private User updatedBy;

    @CreatedDate // audit mekanizmasıile builder.default conflict olur o yüzden builder kaldırıldı
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAtDate;

    @Column(nullable = false)
    @LastModifiedDate // Güncellenme zamanını otomatik günceller
    private LocalDateTime updatedDate;


    
}
