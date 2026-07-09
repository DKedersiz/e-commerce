package com.dogukan.ecommerce.dto.response;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
public class ReviewResponse {
    private Long id;
    private Long productId;
    private Integer rating;
    private String comment;
    private String userName;
    private LocalDateTime createdAt;
}
