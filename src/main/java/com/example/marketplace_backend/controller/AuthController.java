package com.example.marketplace_backend.controller;

import com.example.marketplace_backend.DTO.Requests.Jwt.RefreshTokenRequest;
import com.example.marketplace_backend.Model.RefreshToken;
import com.example.marketplace_backend.Model.User;
import com.example.marketplace_backend.Repositories.RefreshTokenRepository;
import com.example.marketplace_backend.Repositories.UserRepository;
import com.example.marketplace_backend.Service.Impl.auth.ExternalOAuth2ServiceImpl;
import com.example.marketplace_backend.Service.Impl.JwtService;
import com.example.marketplace_backend.Service.Impl.auth.UserServiceImpl;
import com.example.marketplace_backend.DTO.Requests.Jwt.LoginRequest;
import com.example.marketplace_backend.DTO.Requests.Jwt.OAuth2TokenRequest;
import com.example.marketplace_backend.DTO.Requests.Jwt.RegisterRequest;
import com.example.marketplace_backend.DTO.Responses.Jwt.JwtResponse;

import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@AllArgsConstructor
public class AuthController {

    private AuthenticationManager authenticationManager;
    private UserRepository userRepository;
    private JwtService jwtService;
    private RefreshTokenRepository refreshTokenRepository;
    private UserServiceImpl userService;
    private ExternalOAuth2ServiceImpl externalOAuth2Service;

    @PostMapping("/register")
    public ResponseEntity<JwtResponse> register(@RequestBody RegisterRequest request) {
        JwtResponse jwtResponse = userService.register(request);
        return ResponseEntity.ok(jwtResponse);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest, HttpServletResponse response) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
            );

            User user = userRepository.findByEmail(loginRequest.getEmail())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            String accessToken = jwtService.generateAccessToken(user);
            String refreshToken = jwtService.generateRefreshToken(user);

            refreshTokenRepository.deleteByUser(user);

            RefreshToken rt = new RefreshToken();
            rt.setUser(user);
            rt.setToken(refreshToken);
            rt.setExpiryDate(Instant.now().plusMillis(2592000000L));
            refreshTokenRepository.save(rt);

            setCookie(response, "accessToken", accessToken, 3600);
            setCookie(response, "refreshToken", refreshToken, 2592000);

            return ResponseEntity.ok(Map.of(
                    "accessToken", accessToken,
                    "refreshToken", refreshToken,
                    "email", user.getEmail()
            ));
        } catch (BadCredentialsException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }
    }

    @GetMapping("/oauth2/success")
    public ResponseEntity<?> oauth2Success(@AuthenticationPrincipal OAuth2User oauth2User, HttpServletResponse response) {
        String email = oauth2User.getAttribute("email");

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found after OAuth2 login"));
        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        refreshTokenRepository.deleteByUser(user);

        RefreshToken token = new RefreshToken();
        token.setUser(user);
        token.setToken(refreshToken);
        token.setExpiryDate(Instant.now().plusMillis(2592000000L));
        refreshTokenRepository.save(token);

        setCookie(response, "accessToken", accessToken, 3600);
        setCookie(response, "refreshToken", refreshToken, 2592000);

        return ResponseEntity.ok(Map.of(
                "accessToken", accessToken,
                "refreshToken", refreshToken,
                "email", user.getEmail(),
                "name", user.getName()
        ));
    }

    @PostMapping("/oauth2/token")
    public ResponseEntity<?> oauth2TokenLogin(@RequestBody OAuth2TokenRequest request, HttpServletResponse response) {
        try {
            User user = externalOAuth2Service.processOAuth2Token(request.getToken(), request.getProvider());

            String accessToken = jwtService.generateAccessToken(user);
            String refreshToken = jwtService.generateRefreshToken(user);

            refreshTokenRepository.deleteByUser(user);

            RefreshToken token = new RefreshToken();
            token.setUser(user);
            token.setToken(refreshToken);
            token.setExpiryDate(Instant.now().plusMillis(2592000000L));
            refreshTokenRepository.save(token);

            setCookie(response, "accessToken", accessToken, 3600);
            setCookie(response, "refreshToken", refreshToken, 2592000);

            return ResponseEntity.ok(Map.of(
                    "email", user.getEmail(),
                    "name", user.getName(),
                    "accessToken", accessToken,
                    "refreshToken", refreshToken
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("OAuth2 Login failed: " + e.getMessage());
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(
            @CookieValue(value = "refreshToken", required = false) String refreshToken,
            HttpServletResponse response
    ) {
        if (refreshToken != null) {
            refreshTokenRepository.deleteByToken(refreshToken);
        }

        clearCookie(response, "accessToken");
        clearCookie(response, "refreshToken");

        return ResponseEntity.ok("Logged out successfully");
    }

    private void setCookie(HttpServletResponse response, String name, String value, int maxAgeSeconds) {
//         ====== PROD версия ======
        String cookie = String.format(
                "%s=%s; Path=/; Max-Age=%d; HttpOnly; Secure; SameSite=None; Domain=baistore.net",
                name, value, maxAgeSeconds
        );
        response.addHeader("Set-Cookie", cookie);

        // ====== DEV версия ======
//        String cookie = String.format(
//                "%s=%s; Path=/; Max-Age=%d; HttpOnly",
//                name, value, maxAgeSeconds
//        );
        response.addHeader("Set-Cookie", cookie);

    }

    private void clearCookie(HttpServletResponse response, String name) {
        // ====== PROD версия ======
        String cookie = String.format(
                "%s=; Path=/; Max-Age=0; HttpOnly; Secure; SameSite=None; Domain=baistore.net",
                name
        );
        response.addHeader("Set-Cookie", cookie);

//        // ====== DEV версия ======
//
//        String cookie = String.format(
//                "%s=; Path=/; Max-Age=0; HttpOnly",
//                name
//        );
        response.addHeader("Set-Cookie", cookie);
    }


    @PostMapping("/refresh")
    public ResponseEntity<?> refreshAccessToken(
            @RequestBody RefreshTokenRequest request,
            @CookieValue(value = "refreshToken", required = false) String refreshTokenCookie,
            HttpServletResponse response
    ) {
        try {
            if (refreshTokenCookie == null || request.getAccessToken() == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Missing tokens");
            }

            // Проверяем refresh токен в базе
            RefreshToken savedToken = refreshTokenRepository.findByToken(refreshTokenCookie)
                    .orElseThrow(() -> new RuntimeException("Invalid refresh token"));

            // Проверяем срок действия refresh токена
            if (savedToken.getExpiryDate().isBefore(Instant.now())) {
                refreshTokenRepository.delete(savedToken);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Refresh token expired");
            }

            // Проверяем accessToken: валиден, но истёк (если у тебя есть такая логика)
            String userEmail = jwtService.extractUsername(request.getAccessToken());

            User user = userRepository.findByEmail(userEmail)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // Генерация нового access токена
            String newAccessToken = jwtService.generateAccessToken(user);

            // Устанавливаем новый accessToken в cookie
            setCookie(response, "accessToken", newAccessToken, 3600);

            return ResponseEntity.ok(Map.of(
                    "accessToken", newAccessToken
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token refresh failed: " + e.getMessage());
        }
    }

}