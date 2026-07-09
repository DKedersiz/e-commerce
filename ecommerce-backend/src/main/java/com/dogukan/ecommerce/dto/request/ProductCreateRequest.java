package com.dogukan.ecommerce.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ProductCreateRequest {

    @NotBlank(message = "Ürün adı boş bırakılamaz.")
    private String name;

    private String description;

    @NotNull(message = "Ürün fiyatı boş olamaz.")
    @Positive(message = "Ürün fiyatı sıfırdan büyük olmalıdır.")
    private BigDecimal price;

    @NotNull(message = "Stok bilgisi boş olamaz.")
    @Positive(message = "Stok adedi sıfırdan büyük olmalıdır.")
    private Integer stock;

    private Long categoryId;
}
