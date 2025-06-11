package com.example.marketplace_backend.controller;



import com.example.marketplace_backend.Model.RefreshToken;
import com.example.marketplace_backend.Model.User;
import com.example.marketplace_backend.Repositories.RefreshTokenRepository;
import com.example.marketplace_backend.Repositories.UserRepository;
import com.example.marketplace_backend.Service.Impl.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.Map;

@RestController
@RequestMapping("/token")
public class TokenController {

    JwtService jwtService;
    RefreshTokenRepository refreshRepo;
    UserRepository userRepo;

    public TokenController(JwtService jwtService, RefreshTokenRepository refreshRepo, UserRepository userRepo) {
        this.jwtService = jwtService;
        this.refreshRepo = refreshRepo;
        this.userRepo = userRepo;
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshAccessToken(@RequestBody Map<String, String> request) {
        String refreshToken = request.get("refreshToken");

        if (!jwtService.isTokenValid(refreshToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid refresh token");
        }

        String email = jwtService.getEmailFromToken(refreshToken);
        RefreshToken savedToken = refreshRepo.findByToken(refreshToken)
                .orElseThrow(() -> new RuntimeException("Refresh not found"));

        if (savedToken.getExpiryDate().isBefore(Instant.now())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Refresh expired");
        }

        User user = userRepo.findByEmail(email).orElseThrow();
        String newAccess = jwtService.generateAccessToken(user);

        return ResponseEntity.ok(Map.of("accessToken", newAccess));
    }
}

