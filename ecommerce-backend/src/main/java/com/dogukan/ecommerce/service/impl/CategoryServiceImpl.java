package com.dogukan.ecommerce.service.impl;

import com.dogukan.ecommerce.dto.request.CategoryCreateRequest;
import com.dogukan.ecommerce.dto.response.CategoryResponse;
import com.dogukan.ecommerce.entity.Category;
import com.dogukan.ecommerce.exception.BusinessException;
import com.dogukan.ecommerce.exception.helper.ErrorType;
import com.dogukan.ecommerce.mapper.CategoryMapper;
import com.dogukan.ecommerce.repository.CategoryRepository;
import com.dogukan.ecommerce.service.CategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Override
    @Transactional
    public CategoryResponse createCategory(CategoryCreateRequest request) {
        log.info("Yeni kategori oluşturuluyor: {}", request.getName());

        if (categoryRepository.existsByName(request.getName()) || categoryRepository.existsBySlug(request.getSlug())) {
            log.warn("Kategori oluşturma başarısız! İsim veya slug zaten kullanımda: {}", request.getName());
            throw new BusinessException(ErrorType.CATEGORY_ALREADY_EXISTS);
        }
        Category category = categoryMapper.toEntity(request);
        Category saved = categoryRepository.save(category);

        log.info("Kategori başarıyla oluşturuldu. ID: {}", saved.getId());
        return categoryMapper.toResponse(saved);
    }
    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponse> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(categoryMapper::toResponse)
                .collect(Collectors.toList());
    }
    @Override
    @Transactional(readOnly = true)
    public CategoryResponse getCategoryBySlug(String slug) {
        Category category = categoryRepository.findBySlug(slug)
                .orElseThrow(() -> new BusinessException(ErrorType.CATEGORY_NOT_FOUND));

        return categoryMapper.toResponse(category);
    }
}
