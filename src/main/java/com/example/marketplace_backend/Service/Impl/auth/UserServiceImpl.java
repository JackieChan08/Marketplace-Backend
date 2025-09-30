package com.example.marketplace_backend.Service.Impl.auth;

import com.example.marketplace_backend.Repositories.CartRepository;
import com.example.marketplace_backend.Repositories.FavoriteRepository;
import com.example.marketplace_backend.Repositories.RefreshTokenRepository;
import com.example.marketplace_backend.Repositories.UserRepository;
import com.example.marketplace_backend.DTO.Requests.Jwt.RegisterRequest;
import com.example.marketplace_backend.DTO.Responses.models.UserResponse;
import com.example.marketplace_backend.DTO.Responses.Jwt.JwtResponse;
import com.example.marketplace_backend.Service.Impl.BaseServiceImpl;
import com.example.marketplace_backend.Service.Impl.ConverterService;
import com.example.marketplace_backend.Service.Impl.JwtService;
import com.example.marketplace_backend.enums.Role;
import com.example.marketplace_backend.Model.*;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class UserServiceImpl extends BaseServiceImpl<User, UUID> {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final ConverterService converterService;
    private final CartRepository cartRepository;
    private final FavoriteRepository favoriteRepository;

    @Value("${jwt.refresh.expiration}")
    private long refreshExpiration;

    protected UserServiceImpl(UserRepository repository,
                              PasswordEncoder passwordEncoder,
                              JwtService jwtService,
                              RefreshTokenService refreshTokenService,
                              ConverterService converterService,
                              CartRepository cartRepository,
                              FavoriteRepository favoriteRepository) {
        super(repository);
        this.userRepository = repository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.refreshTokenService = refreshTokenService;
        this.converterService = converterService;
        this.cartRepository = cartRepository;
        this.favoriteRepository = favoriteRepository;
    }

    @Transactional
    public JwtResponse register(RegisterRequest request) {
        if (request.getEmail() == null || request.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be empty");
        }

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalArgumentException("User with email " + request.getEmail() + " already exists");
        }

        log.info("Registering new user: {}", request.getEmail());

        User saved = userRepository.save(
                User.builder()
                        .email(request.getEmail())
                        .name(request.getName())
                        .password(passwordEncoder.encode(request.getPassword()))
                        .role(Role.USER)
                        .build()
        );

        Cart cart = Cart.builder()
                .user(saved)
                .build();
        cartRepository.save(cart);

        Favorite favorite = Favorite.builder()
                .user(saved)
                .build();
        favoriteRepository.save(favorite);

        String accessToken = jwtService.generateAccessToken(saved);
        String refreshToken = jwtService.generateRefreshToken(saved);

        refreshTokenService.createOrUpdateRefreshToken(
                saved,
                refreshToken,
                Instant.now().plusMillis(refreshExpiration)
        );

        return new JwtResponse(accessToken, refreshToken);
    }

    public List<Order> ordersByUser(User user) {
        return userRepository.ordersByUser(user);
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }

    public Page<UserResponse> searchUsers(String query, Pageable pageable) {
        Page<User> users = userRepository.searchUsers(query, pageable);
        return users.map(converterService::convertToUserResponse);
    }

    public Page<UserResponse> findAll(Pageable pageable) {
        Page<User> users = userRepository.findAll(pageable);
        return users.map(converterService::convertToUserResponse);
    }

    public User getCurrentUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}