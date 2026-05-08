package com.dogukan.ecommerce.controller;

import com.dogukan.ecommerce.dto.request.ProductCreateRequest;
import com.dogukan.ecommerce.dto.response.ProductResponse;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/v1/products")
public interface ProductController {

    @PostMapping("/create")
    ResponseEntity<ProductResponse> createProduct(@Valid @RequestBody ProductCreateRequest request);

    @GetMapping("/{id}")
    ResponseEntity<ProductResponse> getProductById(@PathVariable Long id);

    @GetMapping
    ResponseEntity<Page<ProductResponse>> getAllProducts(
            @PageableDefault(size = 20, sort = "name") @ParameterObject Pageable pageable);

    @DeleteMapping("/delete/{id}")
    ResponseEntity<Void> deleteProduct(@PathVariable Long id);
}
