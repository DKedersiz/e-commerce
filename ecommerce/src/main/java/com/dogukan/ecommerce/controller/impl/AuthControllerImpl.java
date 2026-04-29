package com.dogukan.ecommerce.controller.impl;

import com.dogukan.ecommerce.controller.AuthController;
import com.dogukan.ecommerce.dto.request.LoginRequest;
import com.dogukan.ecommerce.dto.request.RegisterRequest;
import com.dogukan.ecommerce.dto.response.AuthResponse;
import com.dogukan.ecommerce.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthControllerImpl implements AuthController {

    private final AuthService authService;

    @Override
    public ResponseEntity<AuthResponse> register(RegisterRequest registerRequest) {
        return new ResponseEntity<>(authService.register(registerRequest), HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<AuthResponse> login(LoginRequest loginRequest) {
        return ResponseEntity.ok(authService.login(loginRequest));
    }
}
