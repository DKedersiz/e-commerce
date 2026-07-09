package com.dogukan.ecommerce.exception;

import com.dogukan.ecommerce.exception.helper.ErrorType;
import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {

    private final ErrorType errorType;

    public BusinessException(ErrorType errorType) {
        super(errorType.getMessage());
        this.errorType = errorType;
    }
}
