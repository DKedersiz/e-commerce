package com.dogukan.ecommerce.service.impl;

import com.dogukan.ecommerce.dto.request.AiChatRequest;
import com.dogukan.ecommerce.dto.response.AiChatResponse;
import com.dogukan.ecommerce.entity.Product;
import com.dogukan.ecommerce.repository.ProductRepository;
import com.dogukan.ecommerce.service.AiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import tools.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Slf4j
@RequiredArgsConstructor
@Service
public class AiServiceImpl implements AiService {

    @Value("${spring.gemini.api.key}")
    private String geminiApiKey;

    @Value("${spring.gemini.api.url}")
    private String geminiApiUrl;

    private final ProductRepository productRepository;
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper;

    @Override
    public AiChatResponse chatWithAi(AiChatRequest request) {
        try {
            List<Product> allProducts = productRepository.findAll();
            String productsJson = allProducts.stream()
                    .filter(p -> p.getStock() > 0)
                    .map(p -> String.format("{id:%d, name:'%s', price:%s, category:'%s'}",
                            p.getId(), p.getName(), p.getPrice(), p.getCategory() != null ? p.getCategory().getName() : "Genel"))
                    .collect(Collectors.joining(", "));

            String prompt = String.format(
                    "Sen DogukanShop e-ticaret sitesinin yapay zeka satış danışmanısın. " +
                            "Müşteriye çok samimi, kısa ve profesyonel cevaplar vermelisin. " +
                            "Aşağıda mağazamızdaki ürünlerin listesi var: [%s]. " +
                            "Müşterinin sorusu: '%s'. " +
                            "Bana SADECE JSON formatında şu yapıda cevap ver: " +
                            "{\"message\": \"Cevabın\", \"recommendedProductIds\": [Önerilen ürün ID'leri (varsa)]}",
                    productsJson, request.getMessage()
            );

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("contents", List.of(
                    Map.of("parts", List.of(Map.of("text", prompt)))
            ));
            requestBody.put("generationConfig", Map.of("response_mime_type", "application/json"));

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            String url = geminiApiUrl + "?key=" + geminiApiKey;
            Map<String, Object> response = restTemplate.postForObject(url, entity, Map.class);

            List<Map<String, Object>> candidates = (List<Map<String, Object>>) response.get("candidates");
            Map<String, Object> content = (Map<String, Object>) candidates.get(0).get("content");
            List<Map<String, Object>> parts = (List<Map<String, Object>>) content.get("parts");
            String aiJsonResponseText = (String) parts.get(0).get("text");

            return objectMapper.readValue(aiJsonResponseText, AiChatResponse.class);
        } catch (Exception e) {
            log.error("AI ile konuşurken hata oluştu: ", e);
            return new AiChatResponse("Şu an sistemsel bir yoğunluk yaşıyorum, lütfen daha sonra tekrar deneyin.", List.of());
        }
    }
}
