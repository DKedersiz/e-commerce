package com.dogukan.ecommerce.util.config;

import com.dogukan.ecommerce.entity.Category;
import com.dogukan.ecommerce.entity.Product;
import com.dogukan.ecommerce.entity.User;
import com.dogukan.ecommerce.repository.CategoryRepository;
import com.dogukan.ecommerce.repository.ProductRepository;
import com.dogukan.ecommerce.repository.UserRepository;
import com.dogukan.ecommerce.util.enums.Role;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        seedUsers();
        seedCategoriesAndProducts();
    }

    private void seedUsers() {
        if (userRepository.count() == 0) {
            log.info("Veritabanında kullanıcı bulunamadı. Örnek kullanıcılar oluşturuluyor...");

            User admin = User.builder()
                    .firstName("Admin")
                    .lastName("User")
                    .email("admin@ecommerce.com")
                    .password(passwordEncoder.encode("admin"))
                    .role(Role.ADMIN)
                    .build();

            User customer = User.builder()
                    .firstName("Test")
                    .lastName("User")
                    .email("user@ecommerce.com")
                    .password(passwordEncoder.encode("user"))
                    .role(Role.USER)
                    .build();

            userRepository.saveAll(List.of(admin, customer));
            log.info("Örnek kullanıcılar başarıyla oluşturuldu.");
            log.info("Admin Hesabı: admin@ecommerce.com / admin");
            log.info("Test Kullanıcı Hesabı: user@ecommerce.com / user");
        }
    }

    private void seedCategoriesAndProducts() {
        if (categoryRepository.count() == 0 && productRepository.count() == 0) {
            log.info("Kategoriler ve ürünler bulunamadı. Örnek veriler yükleniyor...");

            // 1. Kategorileri Oluştur
            Category elektronik = Category.builder()
                    .name("Elektronik")
                    .slug("elektronik")
                    .description("Akıllı telefonlar, bilgisayarlar ve elektronik aksesuarlar")
                    .build();

            Category moda = Category.builder()
                    .name("Moda")
                    .slug("moda")
                    .description("Giyim, ayakkabı ve aksesuar ürünleri")
                    .build();

            Category kitap = Category.builder()
                    .name("Kitap")
                    .slug("kitap")
                    .description("Romanlar, eğitim ve kişisel gelişim kitapları")
                    .build();

            categoryRepository.saveAll(List.of(elektronik, moda, kitap));

            // 2. Ürünleri Oluştur
            Product iphone = Product.builder()
                    .name("iPhone 15 Pro")
                    .description("Apple A17 Pro çip, 128GB depolama, titanyum kasa akıllı telefon.")
                    .price(new BigDecimal("75000.00"))
                    .stock(15)
                    .category(elektronik)
                    .build();

            Product macbook = Product.builder()
                    .name("MacBook Air M3")
                    .description("Apple M3 işlemci, 8GB RAM, 256GB SSD, 13.6 inç Liquid Retina ekran laptop.")
                    .price(new BigDecimal("55000.00"))
                    .stock(8)
                    .category(elektronik)
                    .build();

            Product shirt = Product.builder()
                    .name("Oversize Siyah T-Shirt")
                    .description("%100 premium pamuk kumaş, unisex kesim rahat oversize t-shirt.")
                    .price(new BigDecimal("499.99"))
                    .stock(120)
                    .category(moda)
                    .build();

            Product book = Product.builder()
                    .name("Effective Java (3. Edisyon)")
                    .description("Joshua Bloch tarafından kaleme alınmış, en iyi Java kodlama pratikleri rehberi.")
                    .price(new BigDecimal("450.00"))
                    .stock(50)
                    .category(kitap)
                    .build();

            productRepository.saveAll(List.of(iphone, macbook, shirt, book));
            log.info("Örnek kategoriler ve ürünler başarıyla veritabanına kaydedildi.");
        }
    }
}
