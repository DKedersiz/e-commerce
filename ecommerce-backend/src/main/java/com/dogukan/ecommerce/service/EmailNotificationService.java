package com.dogukan.ecommerce.service;

import java.math.BigDecimal;

public interface EmailNotificationService {
    void sendOrderConfirmation(String email, Long orderId, BigDecimal totalAmount);
}
