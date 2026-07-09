package com.dogukan.ecommerce.controller.impl;

import com.dogukan.ecommerce.controller.ReviewController;
import com.dogukan.ecommerce.dto.request.ReviewCreateRequest;
import com.dogukan.ecommerce.dto.response.ReviewResponse;
import com.dogukan.ecommerce.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ReviewControllerImpl implements ReviewController {

    private final ReviewService reviewService;

    @Override
    public ResponseEntity<ReviewResponse> createReview(ReviewCreateRequest request, UserDetails userDetails) {
        return ResponseEntity.ok(reviewService.createReview(request, userDetails.getUsername()));
    }

    @Override
    public ResponseEntity<List<ReviewResponse>> getReviewsByProduct(Long productId) {
        return ResponseEntity.ok(reviewService.getReviewsByProduct(productId));
    }

    @Override
    public ResponseEntity<Double> getAverageRating(Long productId) {
        return ResponseEntity.ok(reviewService.getAverageRating(productId));
    }
}
