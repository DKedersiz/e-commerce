package com.dogukan.ecommerce.exception.helper;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorType {
    INTERNAL_SERVER_ERROR(1,"Sunucuda beklenmeyen bir hata oluştu.",HttpStatus.INTERNAL_SERVER_ERROR),
    PRODUCT_NOT_FOUND(2,"Ürün bulunamadı.",HttpStatus.NOT_FOUND),
    EMAIL_ALREADY_EXISTS(3,"Bu email ile oluşturulmuş bir hesap mevcut.",HttpStatus.BAD_REQUEST),
    USER_NOT_FOUND(4,"Kullanıcı bulunamadı.",HttpStatus.NOT_FOUND);

    private final int code;
    private final String message;
    private final HttpStatus httpStatus;
}
