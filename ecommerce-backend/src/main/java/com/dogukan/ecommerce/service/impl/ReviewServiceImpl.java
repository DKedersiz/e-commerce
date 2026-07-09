package com.dogukan.ecommerce.service.impl;

import com.dogukan.ecommerce.dto.request.ReviewCreateRequest;
import com.dogukan.ecommerce.dto.response.ReviewResponse;
import com.dogukan.ecommerce.entity.Product;
import com.dogukan.ecommerce.entity.Review;
import com.dogukan.ecommerce.entity.User;
import com.dogukan.ecommerce.exception.BusinessException;
import com.dogukan.ecommerce.exception.helper.ErrorType;
import com.dogukan.ecommerce.mapper.ReviewMapper;
import com.dogukan.ecommerce.repository.ProductRepository;
import com.dogukan.ecommerce.repository.ReviewRepository;
import com.dogukan.ecommerce.repository.UserRepository;
import com.dogukan.ecommerce.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final ProductRepository productRepository;
    private final ReviewMapper reviewMapper;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public ReviewResponse createReview(ReviewCreateRequest request, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(ErrorType.USER_NOT_FOUND));

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new BusinessException(ErrorType.PRODUCT_NOT_FOUND));

        if (reviewRepository.existsByProductIdAndUserId(product.getId(), user.getId())) {
            throw new BusinessException(ErrorType.REVIEW_ALREADY_EXISTS);
        }

        Review review = reviewMapper.toEntity(request);
        review.setProduct(product);
        review.setUser(user);

        Review savedReview = reviewRepository.save(review);
        return reviewMapper.toResponse(savedReview);
    }

    @Override
    public ReviewResponse createReview(ReviewCreateRequest request, User user) {
        return null;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReviewResponse> getReviewsByProduct(Long productId) {
        if (!productRepository.existsById(productId)) {
            throw new BusinessException(ErrorType.PRODUCT_NOT_FOUND);
        }

        List<Review> reviews = reviewRepository.findByProductIdOrderByCreatedAtDesc(productId);
        return reviews.stream()
                .map(reviewMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Double getAverageRating(Long productId) {
        if (!productRepository.existsById(productId)) {
            throw new BusinessException(ErrorType.PRODUCT_NOT_FOUND);
        }
        return reviewRepository.getAverageRatingByProductId(productId);
    }
}
