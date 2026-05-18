package com.dogukan.ecommerce.service;

import com.dogukan.ecommerce.dto.request.ProductCreateRequest;
import com.dogukan.ecommerce.dto.response.ProductResponse;
import com.dogukan.ecommerce.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Map;

public interface ProductService {
    ProductResponse createProduct(ProductCreateRequest product);

    ProductResponse getProductById(Long id);

    Page<ProductResponse> getAllProducts(Pageable pageable);

    void deleteProduct(Long id);

    Map<Long, Product> decreaseStocksAndGet(Map<Long, Integer> productQuantities);

    void increaseStock(Long productId, Integer quantity);
}
