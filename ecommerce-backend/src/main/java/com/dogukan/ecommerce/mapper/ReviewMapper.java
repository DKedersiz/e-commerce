package com.dogukan.ecommerce.mapper;

import com.dogukan.ecommerce.dto.request.ReviewCreateRequest;
import com.dogukan.ecommerce.dto.response.ReviewResponse;
import com.dogukan.ecommerce.entity.Review;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ReviewMapper {

    @Mapping(target = "product", ignore = true)
    @Mapping(target = "user", ignore = true)
    Review toEntity(ReviewCreateRequest request);

    @Mapping(source = "product.id", target = "productId")
    @Mapping(target = "userName", expression = "java(review.getUser().getFirstName() + \" \" + review.getUser().getLastName())")
    ReviewResponse toResponse(Review review);
}
