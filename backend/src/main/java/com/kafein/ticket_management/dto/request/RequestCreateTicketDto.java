package com.kafein.ticket_management.dto.request;

import java.util.UUID;

import com.kafein.ticket_management.model.enums.TicketPriority;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record RequestCreateTicketDto(
    
    @Schema(example = "Örnek Başlık")
    @NotBlank(message = "Bilet başlığı boş olamaz")
    @Size(min = 3, max = 150, message = "Başlık 3-150 karakter arasında olmalıdır")
    String title,

    @Schema(example = "Bilete uygun olarak açıklamayı oluşturmalısınız.")
    @Size(min =3, max = 2000, message = "Açıklama 3-2000 karakter arasında olmalıdır")
    String description,

    @Schema(example = "LOW")
    @NotNull(message = "Öncelik seviyesi belirtilmelidir")
    TicketPriority priority,

    @Schema(description = "Bileti sistemde bulunan kullanıcı ID'si(UUID) üzerinden atamalısınız", example = "21e06296-fbc7-45a0-bfc6-bacff7942a0a")
    @NotNull(message = "Atanacak kullanıcıyı belirleme zorunludur")
    UUID assignedToId
) {}
