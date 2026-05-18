package com.dogukan.ecommerce.dto.response;


public record PaymentResult(boolean isSuccess, String failureReason) {
}
