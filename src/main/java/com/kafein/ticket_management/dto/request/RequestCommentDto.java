package com.kafein.ticket_management.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RequestCommentDto (

    @Schema(example = "Yorum içeriğini boş bırakmadan 2 ile 500 karakter arasında yorum içeriği oluşturmalısınız.")
    @NotBlank(message = "Yorum içeriği boş olamaz")
    @Size(min = 2, max = 500, message = "Yorum 2 ile 500 karakter arasında olmalıdır")
    String content

){}
