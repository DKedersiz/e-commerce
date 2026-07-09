package com.dogukan.ecommerce.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RequestMapping("/api/v1/payments")
public interface PaymentController {

    @PostMapping("/create-checkout-session/{orderId}")
    ResponseEntity<Map<String,String>> createCheckoutSession(@PathVariable Long orderId);

    @PostMapping("/webhook")
    ResponseEntity<Void> stripeWebhook(@RequestBody String payload, @RequestHeader("Stripe-Signature") String sigHeader);

}
