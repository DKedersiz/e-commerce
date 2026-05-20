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

    ProductResponse toResponse(Product product);
}
