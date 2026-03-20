package com.kafein.ticket_management.model;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.kafein.ticket_management.model.enums.AuditLogStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "audit_logs", indexes = {
    @Index(name ="idx_performed_by", columnList = "performed_by"),
    @Index(name ="idx_created_at_date", columnList = "created_at_date")
})
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, updatable = false)
    private String operation; // işlem adı

    @Column(nullable = false, updatable = false)
    private String details; 

    @Column(name = "performed_by", nullable = false)
    private String performedBy; // email tutulacak

    @Column(nullable = false, updatable = false)
    @Enumerated(EnumType.STRING)
    private AuditLogStatus status;

    @Column(nullable = true)
    private String errorMessage; // Eğer hata varsa sebebi

    @CreatedDate
    @Column(name = "created_at_date",nullable = false, updatable = false)
    private LocalDateTime createdAtDate;

}
