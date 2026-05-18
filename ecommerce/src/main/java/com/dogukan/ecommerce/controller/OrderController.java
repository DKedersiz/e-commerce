package com.dogukan.ecommerce.controller;

import com.dogukan.ecommerce.dto.request.OrderCreateRequest;
import com.dogukan.ecommerce.dto.request.PaymentRequest;
import com.dogukan.ecommerce.dto.response.OrderResponse;
import com.dogukan.ecommerce.dto.response.PaymentResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/api/v1/orders")
public interface OrderController {

    @PostMapping("/{id}/pay")
    ResponseEntity<PaymentResponse> payOrder(@PathVariable Long id, @RequestBody PaymentRequest paymentRequest);

    @PostMapping("/create")
    ResponseEntity<OrderResponse> createOrder(@RequestBody OrderCreateRequest request, @AuthenticationPrincipal UserDetails userDetails);
}
