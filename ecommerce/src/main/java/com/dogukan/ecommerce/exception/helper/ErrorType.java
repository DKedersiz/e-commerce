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
    USER_NOT_FOUND(4,"Kullanıcı bulunamadı.",HttpStatus.NOT_FOUND),
    NOT_ENOUGH_STOCK(5,"Yeterli stok bulunamadı.",HttpStatus.BAD_REQUEST),
    EMPTY_CART(6,"Sepet boş olamaz.",HttpStatus.BAD_REQUEST),
    INVALID_ORDER_ITEMS(7,"Invalid order items.",HttpStatus.BAD_REQUEST),
    INVALID_STATUS_TRANSITION(8,"Invalid status transition.",HttpStatus.BAD_REQUEST),
    ORDER_NOT_FOUND(9,"Sipariş bulunamadı.",HttpStatus.NOT_FOUND),
    EMAIL_SENDING_FAILED(10,"E-posta gönderilirken hata oluştu.",HttpStatus.INTERNAL_SERVER_ERROR);


    private final int code;
    private final String message;
    private final HttpStatus httpStatus;
}
