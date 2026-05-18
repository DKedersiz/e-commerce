package com.dogukan.ecommerce.service;

import com.dogukan.ecommerce.dto.request.PaymentRequest;
import com.dogukan.ecommerce.dto.response.PaymentResponse;
import com.dogukan.ecommerce.dto.response.PaymentResult;

import java.math.BigDecimal;

public interface MockPaymentService {
    PaymentResult processPayment(PaymentRequest paymentRequest, BigDecimal amount);
    PaymentResponse payOrder(Long orderId, PaymentRequest request);
}
