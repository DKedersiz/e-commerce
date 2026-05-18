package com.dogukan.ecommerce.service.impl;

import com.dogukan.ecommerce.dto.request.ProductCreateRequest;
import com.dogukan.ecommerce.dto.response.ProductResponse;
import com.dogukan.ecommerce.entity.Product;
import com.dogukan.ecommerce.exception.BusinessException;
import com.dogukan.ecommerce.exception.helper.ErrorType;
import com.dogukan.ecommerce.mapper.ProductMapper;
import com.dogukan.ecommerce.repository.ProductRepository;
import com.dogukan.ecommerce.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductServiceImpl implements ProductService {

    private final ProductMapper productMapper;
    private final ProductRepository productRepository;


    @Override
    @Transactional
    public ProductResponse createProduct(ProductCreateRequest request) {
        log.info("Yeni ürün oluşturuluyor: {}", request.getName());

        Product product = productMapper.toEntity(request);
        Product savedProduct = productRepository.save(product);

        // TODO: Kafka/RabbitMQ - Ürün oluşturuldu event'i fırlatılacak.
        // TODO: Redis - Yeni ürün eklendiği için ürün listesi cache'i temizlenecek (@CacheEvict).

        return productMapper.toResponse(savedProduct);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductResponse getProductById(Long id) {
        // TODO: Redis - Önce cache'e bakılacak (@Cacheable).
        return productRepository.findById(id)
                .map(productMapper::toResponse)
                .orElseThrow(() -> new BusinessException(ErrorType.PRODUCT_NOT_FOUND));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductResponse> getAllProducts(Pageable pageable) {
        // TODO: Redis - Sayfalanmış veya tam liste cache'ten dönecek.
        return productRepository.findAll(pageable)
                .map(productMapper::toResponse);
    }

    @Override
    public void deleteProduct(Long id) {
        log.warn("Ürün siliniyor. ID: {}", id);

        if (!productRepository.existsById(id)) {
            throw new BusinessException(ErrorType.PRODUCT_NOT_FOUND);
        }

        productRepository.deleteById(id);

        // TODO: Kafka/RabbitMQ - Ürün silindi event'i.
        // TODO: Redis - Cache temizleme.
    }

    @Override
    @Transactional
    public Map<Long, Product> decreaseStocksAndGet(Map<Long, Integer> productQuantities) {
        List<Long> productIds = new ArrayList<>(productQuantities.keySet());
        List<Product> products = productRepository.findAllByIdInWithLockOrderByIdAsc(productIds);

        Set<Long> foundIds = products.stream()
                .map(Product::getId)
                .collect(Collectors.toSet());

        List<Long> missingIds = productIds.stream()
                .filter(id -> !foundIds.contains(id))
                .toList();

        if (!missingIds.isEmpty()) {
            throw new BusinessException(ErrorType.PRODUCT_NOT_FOUND);
        }

        Map<Long, Product> productMap = new HashMap<>();
        for (Product product : products) {
            Integer requiredQuantity = productQuantities.get(product.getId());

            if (product.getStock() < requiredQuantity) {
                throw new BusinessException(ErrorType.NOT_ENOUGH_STOCK);
            }

            product.setStock(product.getStock() - requiredQuantity);
            productMap.put(product.getId(), product);
        }

        return productMap;
    }

    @Override
    @Transactional
    public void increaseStock(Long productId, Integer quantity) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new BusinessException(ErrorType.PRODUCT_NOT_FOUND));

        product.setStock(product.getStock() + quantity);
    }
}
