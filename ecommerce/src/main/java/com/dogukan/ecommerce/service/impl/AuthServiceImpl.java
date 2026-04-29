package com.dogukan.ecommerce.service.impl;

import com.dogukan.ecommerce.dto.request.LoginRequest;
import com.dogukan.ecommerce.dto.request.RegisterRequest;
import com.dogukan.ecommerce.dto.response.AuthResponse;
import com.dogukan.ecommerce.entity.User;
import com.dogukan.ecommerce.exception.BusinessException;
import com.dogukan.ecommerce.exception.helper.ErrorType;
import com.dogukan.ecommerce.repository.UserRepository;
import com.dogukan.ecommerce.security.JwtService;
import com.dogukan.ecommerce.service.AuthService;
import com.dogukan.ecommerce.util.enums.Role;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;


    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        log.info("Yeni kullanıcı kaydı başlatıldı: {}", request.getEmail());

        if(userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException(ErrorType.EMAIL_ALREADY_EXISTS);
        }

        User user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .build();

        userRepository.save(user);

        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        log.info("Kullanıcı başarıyla kaydedildi: {}", user.getEmail());

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        log.info("Kullanıcı girişi denemesi: {}", request.getEmail());

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BusinessException(ErrorType.USER_NOT_FOUND));

        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        log.info("Kullanıcı başarıyla giriş yaptı: {}", user.getEmail());

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }
}
