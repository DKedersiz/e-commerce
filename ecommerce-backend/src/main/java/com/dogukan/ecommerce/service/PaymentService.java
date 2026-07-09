package com.dogukan.ecommerce.service;

public interface PaymentService {

    String createCheckoutSession(Long orderId);

    void handleStripeWebhook(String payload, String sigHeader);

}
