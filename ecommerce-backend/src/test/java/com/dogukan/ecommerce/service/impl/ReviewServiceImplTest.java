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
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReviewServiceImplTest {

    @InjectMocks
    private ReviewServiceImpl reviewService;

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ReviewMapper reviewMapper;

    @Test
    void when_createReview_success_thenReturnReviewResponse() {
        // Arrange
        String email = "test@example.com";
        ReviewCreateRequest request = Instancio.create(ReviewCreateRequest.class);
        User user = Instancio.create(User.class);
        Product product = Instancio.create(Product.class);
        Review review = Instancio.create(Review.class);
        ReviewResponse expectedResponse = Instancio.create(ReviewResponse.class);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(productRepository.findById(request.getProductId())).thenReturn(Optional.of(product));
        when(reviewRepository.existsByProductIdAndUserId(product.getId(), user.getId())).thenReturn(false);
        when(reviewMapper.toEntity(request)).thenReturn(review);
        when(reviewRepository.save(any(Review.class))).thenReturn(review);
        when(reviewMapper.toResponse(any(Review.class))).thenReturn(expectedResponse);

        // Act
        ReviewResponse response = reviewService.createReview(request, email);

        // Assert
        assertNotNull(response);
        assertEquals(expectedResponse.getId(), response.getId());
        verify(reviewRepository, times(1)).save(any(Review.class));
    }

    @Test
    void when_createReview_alreadyExists_thenThrowBusinessException() {
        // Arrange
        String email = "test@example.com";
        ReviewCreateRequest request = Instancio.create(ReviewCreateRequest.class);
        User user = Instancio.create(User.class);
        Product product = Instancio.create(Product.class);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(productRepository.findById(request.getProductId())).thenReturn(Optional.of(product));
        when(reviewRepository.existsByProductIdAndUserId(product.getId(), user.getId())).thenReturn(true);

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class, () -> 
                reviewService.createReview(request, email));
        
        assertEquals(ErrorType.REVIEW_ALREADY_EXISTS, exception.getErrorType());
        verify(reviewRepository, never()).save(any(Review.class));
    }
}
