package com.dogukan.ecommerce.service.impl;

import com.dogukan.ecommerce.dto.request.AiChatRequest;
import com.dogukan.ecommerce.dto.response.AiChatResponse;
import com.dogukan.ecommerce.entity.Product;
import com.dogukan.ecommerce.repository.ProductRepository;
import tools.jackson.databind.ObjectMapper;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;
import java.util.List;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AiServiceImplTest {

    private AiServiceImpl aiService;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private ObjectMapper objectMapper;
    @Mock
    private RestTemplate restTemplate;
    @Test
    void when_chatWithAi_success_thenReturnAiChatResponse() throws Exception {
        aiService = new AiServiceImpl(productRepository, objectMapper);

        ReflectionTestUtils.setField(aiService, "geminiApiKey", "test-key");
        ReflectionTestUtils.setField(aiService, "geminiApiUrl", "http://test-url");
        ReflectionTestUtils.setField(aiService, "restTemplate", restTemplate);
        AiChatRequest request = Instancio.create(AiChatRequest.class);

        Product product = Instancio.create(Product.class);
        product.setStock(10);

        when(productRepository.findAll()).thenReturn(List.of(product));
        Map<String, Object> geminiResponse = Map.of(
                "candidates", List.of(
                        Map.of("content", Map.of(
                                "parts", List.of(
                                        Map.of("text", "{\"message\":\"Harika seçim\",\"recommendedProductIds\":[" + product.getId() + "]}")
                                )
                        ))
                )
        );

        when(restTemplate.postForObject(anyString(), any(HttpEntity.class), eq(Map.class)))
                .thenReturn(geminiResponse);
        AiChatResponse expectedResponse = new AiChatResponse("Harika seçim", List.of(product.getId()));
        when(objectMapper.readValue(anyString(), eq(AiChatResponse.class))).thenReturn(expectedResponse);
        // Act
        AiChatResponse result = aiService.chatWithAi(request);
        // Assert
        assertNotNull(result);
        assertEquals("Harika seçim", result.getMessage());
        assertEquals(1, result.getRecommendedProductIds().size());
        assertEquals(product.getId(), result.getRecommendedProductIds().get(0));
    }
}