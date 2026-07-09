package com.dogukan.ecommerce.controller.impl;

import com.dogukan.ecommerce.controller.CategoryController;
import com.dogukan.ecommerce.dto.request.CategoryCreateRequest;
import com.dogukan.ecommerce.dto.response.CategoryResponse;
import com.dogukan.ecommerce.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class CategoryControllerImpl implements CategoryController {
    private final CategoryService categoryService;
    @Override
    public ResponseEntity<List<CategoryResponse>> getAllCategories() {
        return ResponseEntity.ok(categoryService.getAllCategories());
    }
    @Override
    public ResponseEntity<CategoryResponse> getCategoryBySlug(String slug) {
        return ResponseEntity.ok(categoryService.getCategoryBySlug(slug));
    }
    @Override
    public ResponseEntity<CategoryResponse> createCategory(CategoryCreateRequest request) {
        return ResponseEntity.ok(categoryService.createCategory(request));
    }
}
