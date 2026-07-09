package com.dogukan.ecommerce.controller;

import com.dogukan.ecommerce.dto.request.CategoryCreateRequest;
import com.dogukan.ecommerce.dto.response.CategoryResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/v1/categories")
public interface CategoryController {

    @GetMapping
    ResponseEntity<List<CategoryResponse>> getAllCategories();

    @GetMapping("/{slug}")
    ResponseEntity<CategoryResponse> getCategoryBySlug(@PathVariable String slug);

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    ResponseEntity<CategoryResponse> createCategory(@RequestBody CategoryCreateRequest request);
}
