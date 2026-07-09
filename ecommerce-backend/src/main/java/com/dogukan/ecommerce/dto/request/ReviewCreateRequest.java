package com.dogukan.ecommerce.dto.request;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReviewCreateRequest {

    @NotNull(message = "Ürün ID boş olamaz.")
    private Long productId;

    @NotNull(message = "Puan boş olamaz.")
    @Min(value = 1, message = "Puan en az 1 olmalıdır.")
    @Max(value = 5, message = "Puan en fazla 5 olmalıdır.")
    private Integer rating;

    @Size(max = 1000, message = "Yorum en fazla 1000 karakter olabilir.")
    private String comment;
}
