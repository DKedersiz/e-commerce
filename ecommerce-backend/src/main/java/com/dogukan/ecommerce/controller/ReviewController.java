package com.dogukan.ecommerce.controller;

import com.dogukan.ecommerce.dto.request.ReviewCreateRequest;
import com.dogukan.ecommerce.dto.response.ReviewResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/v1/reviews")
public interface ReviewController {

    @PostMapping
    ResponseEntity<ReviewResponse> createReview(
            @Valid @RequestBody ReviewCreateRequest request,
            @AuthenticationPrincipal UserDetails userDetails);

    @GetMapping("/product/{productId}")
    ResponseEntity<List<ReviewResponse>> getReviewsByProduct(@PathVariable Long productId);

    @GetMapping("/product/{productId}/average-rating")
    ResponseEntity<Double> getAverageRating(@PathVariable Long productId);
}
