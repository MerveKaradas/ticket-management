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
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "audit_logs")
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
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAtDate;

}
