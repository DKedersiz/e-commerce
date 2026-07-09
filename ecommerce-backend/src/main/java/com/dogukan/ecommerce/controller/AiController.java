package com.dogukan.ecommerce.controller;

import com.dogukan.ecommerce.dto.request.AiChatRequest;
import com.dogukan.ecommerce.dto.response.AiChatResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/api/v1/ai")
public interface AiController {

    @PostMapping("/chat")
    ResponseEntity<AiChatResponse> chat(@Valid @RequestBody AiChatRequest request);
}