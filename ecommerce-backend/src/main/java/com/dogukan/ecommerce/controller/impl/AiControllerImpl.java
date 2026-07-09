package com.dogukan.ecommerce.controller.impl;

import com.dogukan.ecommerce.controller.AiController;
import com.dogukan.ecommerce.dto.request.AiChatRequest;
import com.dogukan.ecommerce.dto.response.AiChatResponse;
import com.dogukan.ecommerce.service.AiService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class AiControllerImpl implements AiController {

    private final AiService aiService;

    @Override
    public ResponseEntity<AiChatResponse> chat(AiChatRequest request) {
        return ResponseEntity.ok(aiService.chatWithAi(request));
    }
}
