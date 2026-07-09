package com.dogukan.ecommerce.dto.event;

import java.math.BigDecimal;

public record OrderCompletedEvent(Long orderId, String userEmail, BigDecimal totalAmount){}