package com.example.marketplace_backend.Service.Impl;

import com.example.marketplace_backend.Repositories.RefreshTokenRepository;
import com.example.marketplace_backend.Repositories.UserRepository;
import com.example.marketplace_backend.controller.Requests.Jwt.RegisterRequest;
import com.example.marketplace_backend.controller.Responses.Jwt.JwtResponse;
import com.example.marketplace_backend.enums.Role;
import com.example.marketplace_backend.Model.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class UserServiceImpl extends BaseServiceImpl<User, Long>{

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${jwt.refresh.expiration}")
    private long refreshExpiration;

    protected UserServiceImpl(UserRepository repository, PasswordEncoder passwordEncoder, JwtService jwtService, RefreshTokenRepository refreshTokenRepository) {
        super(repository);
        this.userRepository = repository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.refreshTokenRepository = refreshTokenRepository;
    }

    @Transactional
    public JwtResponse register(RegisterRequest request) {

        User saved = userRepository.save(
                User.builder()
                        .email(request.getEmail())
                        .name(request.getName())
                        .password(passwordEncoder.encode(request.getPassword()))
                        .role(Role.USER)
                        .build()
        );

        String accessToken = jwtService.generateAccessToken(saved);
        String refreshTokenStr = jwtService.generateRefreshToken(saved);

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(saved);
        refreshToken.setToken(refreshTokenStr);
        refreshToken.setExpiryDate(Instant.now().plusMillis(refreshExpiration));
        refreshTokenRepository.save(refreshToken);

        return new JwtResponse(accessToken, refreshTokenStr);
    }


    public List<Order> ordersByUser(User user) {
        return userRepository.ordersByUser(user);
    }

}
