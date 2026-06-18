package com.dogukan.ecommerce.service;

import com.dogukan.ecommerce.dto.request.OrderCreateRequest;
import com.dogukan.ecommerce.dto.response.OrderResponse;

public interface OrderService {
    OrderResponse createOrder(OrderCreateRequest request, String userEmail);
    void cancelExpiredOrders();
}
