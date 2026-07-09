package com.dogukan.ecommerce.dto.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class OrderCreateRequest {
    private List<OrderItemRequest> items;
}
