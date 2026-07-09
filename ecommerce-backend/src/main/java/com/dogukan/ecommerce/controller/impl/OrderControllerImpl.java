package com.dogukan.ecommerce.controller.impl;

import com.dogukan.ecommerce.controller.OrderController;
import com.dogukan.ecommerce.dto.request.OrderCreateRequest;
import com.dogukan.ecommerce.dto.request.PaymentRequest;
import com.dogukan.ecommerce.dto.response.OrderResponse;
import com.dogukan.ecommerce.dto.response.PaymentResponse;
import com.dogukan.ecommerce.service.MockPaymentService;
import com.dogukan.ecommerce.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class OrderControllerImpl implements OrderController {
    private final MockPaymentService mockPaymentService;
    private final OrderService  orderService;

    @Override
    public ResponseEntity<PaymentResponse> payOrder(Long id, PaymentRequest paymentRequest) {
        PaymentResponse response = mockPaymentService.payOrder(id,paymentRequest);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<OrderResponse> createOrder(OrderCreateRequest request, UserDetails userDetails) {
        return ResponseEntity.ok(orderService.createOrder(request,userDetails.getUsername()));
    }

    @Override
    public ResponseEntity<List<OrderResponse>> getMyOrders(UserDetails userDetails) {
        return ResponseEntity.ok(orderService.getOrdersByUserEmail(userDetails.getUsername()));
    }

    @Override
    public ResponseEntity<OrderResponse> getOrderById(Long id, UserDetails userDetails) {
        return ResponseEntity.ok(orderService.getOrderById(id,userDetails.getUsername()));
    }
}
