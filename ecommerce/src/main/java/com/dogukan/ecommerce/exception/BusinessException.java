package com.dogukan.ecommerce.exception;

import com.dogukan.ecommerce.exception.helper.ErrorType;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class BusinessException extends RuntimeException {

    private final ErrorType errorType;

    public BusinessException(HttpStatus httpStatus, String message, ErrorType errorType) {
        super(errorType.getMessage());
        this.errorType = errorType;
    }
}
