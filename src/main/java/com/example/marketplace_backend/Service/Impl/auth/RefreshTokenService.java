package com.example.marketplace_backend.Service.Impl.auth;

import com.example.marketplace_backend.Model.RefreshToken;
import com.example.marketplace_backend.Model.User;
import com.example.marketplace_backend.Repositories.RefreshTokenRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public RefreshToken createOrUpdateRefreshToken(User user, String token, Instant expiryDate) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }
        if (token == null || token.trim().isEmpty()) {
            throw new IllegalArgumentException("Token cannot be null or empty");
        }
        if (expiryDate == null || expiryDate.isBefore(Instant.now())) {
            throw new IllegalArgumentException("Expiry date must be in the future");
        }

        Optional<RefreshToken> existingTokenOpt = refreshTokenRepository.findByUser(user);

        RefreshToken refreshToken;
        if (existingTokenOpt.isPresent()) {
            refreshToken = existingTokenOpt.get();
            refreshToken.setToken(token);
            refreshToken.setExpiryDate(expiryDate);
        } else {
            refreshToken = new RefreshToken();
            refreshToken.setUser(user);
            refreshToken.setToken(token);
            refreshToken.setExpiryDate(expiryDate);
        }

        return refreshTokenRepository.save(refreshToken);
    }

    @Transactional
    public boolean validateRefreshToken(String rawToken, RefreshToken storedToken) {
        if (rawToken == null || storedToken == null) {
            return false;
        }

        if (storedToken.getExpiryDate().isBefore(Instant.now())) {
            return false;
        }

        return rawToken.equals(storedToken.getToken());
    }

    @Transactional
    public void deleteByUser(User user) {
        refreshTokenRepository.deleteByUser(user);
    }
}