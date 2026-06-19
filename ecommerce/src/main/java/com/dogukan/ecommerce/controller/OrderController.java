package com.dogukan.ecommerce.controller;

import com.dogukan.ecommerce.dto.request.OrderCreateRequest;
import com.dogukan.ecommerce.dto.request.PaymentRequest;
import com.dogukan.ecommerce.dto.response.OrderResponse;
import com.dogukan.ecommerce.dto.response.PaymentResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/v1/orders")
public interface OrderController {

    @PostMapping("/{id}/pay")
    ResponseEntity<PaymentResponse> payOrder(@PathVariable Long id, @RequestBody PaymentRequest paymentRequest);

    @PostMapping("/create")
    ResponseEntity<OrderResponse> createOrder(@RequestBody OrderCreateRequest request, @AuthenticationPrincipal UserDetails userDetails);

    @GetMapping("/getOrders")
    ResponseEntity<List<OrderResponse>> getMyOrders(@AuthenticationPrincipal UserDetails userDetails);

    @GetMapping("/{id}")
    ResponseEntity<OrderResponse> getOrderById(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails);

}
