package com.dogukan.ecommerce.mapper;

import com.dogukan.ecommerce.dto.request.ProductCreateRequest;
import com.dogukan.ecommerce.dto.response.ProductResponse;
import com.dogukan.ecommerce.entity.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    @Mapping(target = "version",ignore = true)
    Product toEntity(ProductCreateRequest request);

    @org.mapstruct.Mapping(source = "category.name", target = "categoryName")
    @org.mapstruct.Mapping(source = "category.slug", target = "categorySlug")
    @org.mapstruct.Mapping(target = "averageRating", ignore = true)
    @org.mapstruct.Mapping(target = "reviewCount", ignore = true)
    ProductResponse toResponse(Product product);
}
