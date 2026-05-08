package com.dogukan.ecommerce.service;

import com.dogukan.ecommerce.dto.request.OrderCreateRequest;
import com.dogukan.ecommerce.dto.response.OrderResponse;

public interface OrderService {
    public OrderResponse createOrder(OrderCreateRequest request, String userEmail);
}
