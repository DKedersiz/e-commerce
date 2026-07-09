package com.dogukan.ecommerce.service;

import com.dogukan.ecommerce.dto.request.ReviewCreateRequest;
import com.dogukan.ecommerce.dto.response.ReviewResponse;
import com.dogukan.ecommerce.entity.User;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface ReviewService {
    ReviewResponse createReview(ReviewCreateRequest request, String email);

    @Transactional
    ReviewResponse createReview(ReviewCreateRequest request, User user);

    List<ReviewResponse> getReviewsByProduct(Long productId);
    Double getAverageRating(Long productId);
}
