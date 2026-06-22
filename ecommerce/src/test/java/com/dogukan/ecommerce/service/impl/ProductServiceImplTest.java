package com.dogukan.ecommerce.service.impl;

import com.dogukan.ecommerce.dto.request.ProductCreateRequest;
import com.dogukan.ecommerce.dto.response.ProductResponse;
import com.dogukan.ecommerce.entity.Product;
import com.dogukan.ecommerce.exception.BusinessException;
import com.dogukan.ecommerce.exception.helper.ErrorType;
import com.dogukan.ecommerce.mapper.ProductMapper;
import com.dogukan.ecommerce.repository.ProductRepository;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.instancio.Select.field;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

    @InjectMocks
    private ProductServiceImpl productService;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductMapper productMapper;

    @Test
    void when_createProduct_success_then_returnProductResponse() {
        ProductCreateRequest request = Instancio.create(ProductCreateRequest.class);
        Product product = Instancio.create(Product.class);
        ProductResponse expectedResponse = Instancio.create(ProductResponse.class);

        when(productMapper.toEntity(request)).thenReturn(product);
        when(productRepository.save(product)).thenReturn(product);
        when(productMapper.toResponse(product)).thenReturn(expectedResponse);

        ProductResponse result = productService.createProduct(request);

        assertNotNull(result);
        assertEquals(expectedResponse, result);
        verify(productRepository).save(product);
    }

    @Test
    void when_getProductById_success_then_returnProductResponse() {
        Long productId = 1L;
        Product product = Instancio.create(Product.class);
        ProductResponse expectedResponse = Instancio.create(ProductResponse.class);

        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(productMapper.toResponse(product)).thenReturn(expectedResponse);

        ProductResponse result = productService.getProductById(productId);

        assertNotNull(result);
        assertEquals(expectedResponse, result);
    }

    @Test
    void when_getProductById_notFound_then_throwException() {
        when(productRepository.findById(any())).thenReturn(Optional.empty());

        BusinessException ex = assertThrows(BusinessException.class,
                () -> productService.getProductById(99L));

        assertEquals(ErrorType.PRODUCT_NOT_FOUND, ex.getErrorType());
    }

    @Test
    void when_deleteProduct_success_then_deleteFromRepository() {
        Long productId = 1L;

        when(productRepository.existsById(productId)).thenReturn(true);

        productService.deleteProduct(productId);

        verify(productRepository).deleteById(productId);
    }

    @Test
    void when_deleteProduct_notFound_then_throwException() {
        Long productId = 99L;

        when(productRepository.existsById(productId)).thenReturn(false);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> productService.deleteProduct(productId));

        assertEquals(ErrorType.PRODUCT_NOT_FOUND, ex.getErrorType());
        verify(productRepository, never()).deleteById(any());
    }

    @Test
    void when_decreaseStocksAndGet_success_then_returnProductMap() {
        Long productId = 1L;
        int requestedQuantity = 3;
        int currentStock = 10;

        Product product = Instancio.of(Product.class)
                .set(field(Product::getId), productId)
                .set(field(Product::getStock), currentStock)
                .create();

        when(productRepository.findAllByIdInWithLockOrderByIdAsc(List.of(productId)))
                .thenReturn(List.of(product));

        Map<Long, Product> result = productService.decreaseStocksAndGet(Map.of(productId, requestedQuantity));

        assertNotNull(result);
        assertTrue(result.containsKey(productId));
        assertEquals(currentStock - requestedQuantity, result.get(productId).getStock());
    }

    @Test
    void when_decreaseStocksAndGet_productNotFound_then_throwException() {
        Long productId = 1L;

        when(productRepository.findAllByIdInWithLockOrderByIdAsc(any()))
                .thenReturn(List.of());

        BusinessException ex = assertThrows(BusinessException.class,
                () -> productService.decreaseStocksAndGet(Map.of(productId, 1)));

        assertEquals(ErrorType.PRODUCT_NOT_FOUND, ex.getErrorType());
    }

    @Test
    void when_decreaseStocksAndGet_notEnoughStock_then_throwException() {
        Long productId = 1L;
        int currentStock = 2;
        int requestedQuantity = 5;

        Product product = Instancio.of(Product.class)
                .set(field(Product::getId), productId)
                .set(field(Product::getStock), currentStock)
                .create();

        when(productRepository.findAllByIdInWithLockOrderByIdAsc(List.of(productId)))
                .thenReturn(List.of(product));

        BusinessException ex = assertThrows(BusinessException.class,
                () -> productService.decreaseStocksAndGet(Map.of(productId, requestedQuantity)));

        assertEquals(ErrorType.NOT_ENOUGH_STOCK, ex.getErrorType());
    }

    @Test
    void when_increaseStock_success_then_stockIncreased() {
        Long productId = 1L;
        int currentStock = 5;
        int increaseAmount = 3;

        Product product = Instancio.of(Product.class)
                .set(field(Product::getId), productId)
                .set(field(Product::getStock), currentStock)
                .create();

        when(productRepository.findById(productId)).thenReturn(Optional.of(product));

        productService.increaseStock(productId, increaseAmount);

        assertEquals(currentStock + increaseAmount, product.getStock());
    }

    @Test
    void when_increaseStock_productNotFound_then_throwException() {
        when(productRepository.findById(any())).thenReturn(Optional.empty());

        BusinessException ex = assertThrows(BusinessException.class,
                () -> productService.increaseStock(99L, 5));

        assertEquals(ErrorType.PRODUCT_NOT_FOUND, ex.getErrorType());
    }

    @Test
    void when_restoreStockBulk_success_then_repositoryCalledForEachEntry() {
        Map<Long, Integer> productQuantities = Map.of(1L, 3, 2L, 5);

        productService.restoreStockBulk(productQuantities);

        verify(productRepository, times(productQuantities.size()))
                .restoreStockBulk(anyLong(), anyInt());
    }
}