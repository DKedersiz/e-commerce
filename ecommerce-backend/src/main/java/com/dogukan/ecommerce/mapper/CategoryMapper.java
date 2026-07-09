package com.dogukan.ecommerce.mapper;

import com.dogukan.ecommerce.dto.request.CategoryCreateRequest;
import com.dogukan.ecommerce.dto.response.CategoryResponse;
import com.dogukan.ecommerce.entity.Category;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CategoryMapper {
    Category toEntity(CategoryCreateRequest request);
    CategoryResponse toResponse(Category category);
}
