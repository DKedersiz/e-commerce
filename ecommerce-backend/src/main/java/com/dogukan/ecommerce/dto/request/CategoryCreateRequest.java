package com.dogukan.ecommerce.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CategoryCreateRequest {
    @NotBlank(message = "Kategori adı boş olamaz")
    private String name;

    @NotBlank(message = "Slug (URL) boş olamaz")
    private String slug;

    private String description;
}
