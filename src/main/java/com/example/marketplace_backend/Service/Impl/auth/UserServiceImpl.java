package com.example.marketplace_backend.Service.Impl.auth;

import com.example.marketplace_backend.Repositories.RefreshTokenRepository;
import com.example.marketplace_backend.Repositories.UserRepository;
import com.example.marketplace_backend.DTO.Requests.Jwt.RegisterRequest;
import com.example.marketplace_backend.DTO.Responses.models.UserResponse;
import com.example.marketplace_backend.DTO.Responses.Jwt.JwtResponse;
import com.example.marketplace_backend.Service.Impl.BaseServiceImpl;
import com.example.marketplace_backend.Service.Impl.JwtService;
import com.example.marketplace_backend.enums.Role;
import com.example.marketplace_backend.Model.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class UserServiceImpl extends BaseServiceImpl<User, UUID> {

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
    public User findByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }

    public Page<UserResponse> searchUsers(String query, Pageable pageable) {
        Page<User> users = userRepository.searchUsers(query, pageable);
        return users.map(this::convertToResponse);
    }

    private UserResponse convertToResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getName(),
                user.getRole()
        );
    }

}
