package com.dogukan.ecommerce.exception;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class ErrorResponse {
    private LocalDateTime timestamp;
    private int errorCode;
    private String message;
    private String path;
}
