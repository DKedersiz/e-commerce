package com.dogukan.ecommerce.security.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "E-Commerce API",
                description = "Sektör standartlarına uyarak hazırlamaya çalıştığım e-ticaret projesinin back-end'i. Herhangi bir feedback için lütfen iletişime geçin. kedersizd@gmail.com",
                version = "1.0",
                contact = @Contact(
                        name = " Doğukan",
                        email = "kedersizd@gmail.com"
                )
        ),
        security = {
                @SecurityRequirement(name = "bearerAuth")
        }
)
@SecurityScheme(
        name = "bearerAuth",
        description = "Giriş yaptıktan sonra aldığınız Access Token'ı buraya yapıştırın. (Başında Bearer yazmanıza gerek yok, sistem otomatik ekler)",
        scheme = "bearer",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        in = SecuritySchemeIn.HEADER
)
public class OpenApiConfig {
}

