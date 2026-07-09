package com.dogukan.ecommerce.service;

import com.dogukan.ecommerce.dto.request.AiChatRequest;
import com.dogukan.ecommerce.dto.response.AiChatResponse;

public interface AiService {
    AiChatResponse chatWithAi(AiChatRequest request);
}
