package com.dogukan.ecommerce.controller.impl;

import com.dogukan.ecommerce.controller.PaymentController;
import com.dogukan.ecommerce.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class PaymentControllerImpl implements PaymentController {

    private final PaymentService paymentService;

    @Override
    public ResponseEntity<Map<String, String>> createCheckoutSession(Long orderId) {
        String checkoutUrl = paymentService.createCheckoutSession(orderId);
        return ResponseEntity.ok(Map.of("url", checkoutUrl));
    }

    @Override
    public ResponseEntity<Void> stripeWebhook(String payload, String sigHeader) {
        paymentService.handleStripeWebhook(payload, sigHeader);
        return ResponseEntity.ok().build();
    }
}
