package com.dogukan.ecommerce.service;

import com.dogukan.ecommerce.dto.request.OrderCreateRequest;
import com.dogukan.ecommerce.dto.response.OrderResponse;

import java.util.List;

public interface OrderService {
    OrderResponse createOrder(OrderCreateRequest request, String userEmail);

    void cancelExpiredOrders();

    List<OrderResponse> getOrdersByUserEmail(String userEmail);

    OrderResponse getOrderById(Long orderId, String userEmail);
}
