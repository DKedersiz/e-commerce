package com.dogukan.ecommerce.service;

import com.dogukan.ecommerce.dto.request.CategoryCreateRequest;
import com.dogukan.ecommerce.dto.response.CategoryResponse;

import java.util.List;

public interface CategoryService {
    CategoryResponse createCategory(CategoryCreateRequest request);

    List<CategoryResponse> getAllCategories();

    CategoryResponse getCategoryBySlug(String slug);
}
