package com.dogukan.ecommerce.service;

import com.dogukan.ecommerce.dto.request.LoginRequest;
import com.dogukan.ecommerce.dto.request.RegisterRequest;
import com.dogukan.ecommerce.dto.response.AuthResponse;

public interface AuthService {
    AuthResponse register(RegisterRequest registerRequest);
    AuthResponse login(LoginRequest loginRequest);
}
