package com.example.marketplace_backend.Service.Impl.auth;


import com.example.marketplace_backend.Model.RefreshToken;
import com.example.marketplace_backend.Model.User;
import com.example.marketplace_backend.Repositories.RefreshTokenRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;

@Service
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
    }

    public RefreshToken createOrUpdateRefreshToken(User user, String token, Instant expiryDate) {
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
}
