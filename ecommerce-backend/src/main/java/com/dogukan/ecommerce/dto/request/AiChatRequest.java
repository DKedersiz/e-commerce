package com.dogukan.ecommerce.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AiChatRequest {
    @NotBlank(message = "Mesaj boş olamaz.")
    private String message;
}
