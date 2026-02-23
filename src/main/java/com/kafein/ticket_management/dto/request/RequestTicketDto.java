package com.kafein.ticket_management.dto.request;

import java.util.UUID;

import com.kafein.ticket_management.model.enums.TicketPriority;
import com.kafein.ticket_management.model.enums.TicketStatus;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record RequestTicketDto(
    
    @Schema(description = "Bilet başlığını  5-150 karakter arasında oluşturmalısınız. ", example = "UNIT Testleri")
    @NotBlank(message = "Bilet başlığı boş olamaz")
    @Size(min = 5, max = 150, message = "Başlık 5-150 karakter arasında olmalıdır!")
    String title,

    @Schema(description = "Bilet açıklamasını 3-2000 karakter arasında oluşturmalısınız. ", example = "UNIT testlerinin edge caselerini oluşturmalısın.")
    @NotBlank(message = "Bilet açıklaması boş olamaz")
    @Size(min =3, max = 2000, message = "Açıklama çok uzun")
    String description,

    @Schema(implementation = TicketStatus.class, example = "IN_PROGRESS")
    @NotNull(message = "Status durumu belirtilmelidir")
    TicketStatus status,

    @Schema(implementation = TicketPriority.class, example = "MEDIUM")
    @NotNull(message = "Öncelik seviyesi belirtilmelidir")
    TicketPriority priority,

    @Schema(description = "Biletten sorumlu olan kullanıcıyı UUID olarak atama yapmalısınız. ", example = "21e06296-fbc7-45a0-bfc6-bacff7942a0a")
    @NotNull(message = "Atanacak kullanıcıyı belirleme zorunludur")
    UUID assignedToId


) {}
