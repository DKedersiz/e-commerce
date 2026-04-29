package com.dogukan.ecommerce.controller;

import com.dogukan.ecommerce.dto.request.LoginRequest;
import com.dogukan.ecommerce.dto.request.RegisterRequest;
import com.dogukan.ecommerce.dto.response.AuthResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/api/v1/auth")
public interface AuthController {

    @PostMapping("/register")
    ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest registerRequest);

    @PostMapping("/login")
    ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest loginRequest);
}
