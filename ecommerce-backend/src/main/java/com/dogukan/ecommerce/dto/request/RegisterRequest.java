package com.dogukan.ecommerce.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterRequest {

    @NotBlank(message = "Ad alanı boş bırakılamaz.")
    private String firstName;

    @NotBlank(message = "Soyad alanı boş bırakılamaz")
    private String lastName;

    @NotBlank(message = "Email alanı boş bırakılamaz")
    @Email(message = "Geçerli bir email adresi giriniz")
    private String email;

    @NotBlank(message = "Şifre alanı boş bırakılamaz")
    @Size(min = 8, message = "Şifre en az 8 karakter olmalıdır")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).*$", message = "Şifre en az bir büyük harf, bir küçük harf ve bir rakam içermelidir")
    private String password;
}
