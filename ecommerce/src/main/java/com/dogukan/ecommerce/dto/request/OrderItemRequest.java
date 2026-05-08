package com.dogukan.ecommerce.dto.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class OrderItemRequest {
    private Long productId;
    private Integer quantity;
}
