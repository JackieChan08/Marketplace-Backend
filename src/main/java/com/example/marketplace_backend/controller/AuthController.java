package com.example.marketplace_backend.controller;

import com.example.marketplace_backend.DTO.Requests.Jwt.RefreshTokenRequest;
import com.example.marketplace_backend.Model.RefreshToken;
import com.example.marketplace_backend.Model.User;
import com.example.marketplace_backend.Repositories.RefreshTokenRepository;
import com.example.marketplace_backend.Repositories.UserRepository;
import com.example.marketplace_backend.Service.Impl.auth.ExternalOAuth2ServiceImpl;
import com.example.marketplace_backend.Service.Impl.JwtService;
import com.example.marketplace_backend.Service.Impl.auth.RefreshTokenService;
import com.example.marketplace_backend.Service.Impl.auth.UserServiceImpl;
import com.example.marketplace_backend.DTO.Requests.Jwt.LoginRequest;
import com.example.marketplace_backend.DTO.Requests.Jwt.OAuth2TokenRequest;
import com.example.marketplace_backend.DTO.Requests.Jwt.RegisterRequest;
import com.example.marketplace_backend.DTO.Responses.Jwt.JwtResponse;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserServiceImpl userService;
    private final ExternalOAuth2ServiceImpl externalOAuth2Service;
    private final RefreshTokenService refreshTokenService;

    @Value("${jwt.refresh.expiration}")
    private long refreshExpiration;

    @Value("${app.cookie.domain:}")
    private String cookieDomain;

    @Value("${app.cookie.secure:true}")
    private boolean cookieSecure;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        try {
            JwtResponse jwtResponse = userService.register(request);
            return ResponseEntity.ok(jwtResponse);
        } catch (IllegalArgumentException e) {
            log.error("Registration failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/login")
    @Transactional
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest, HttpServletResponse response) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
            );

            User user = userRepository.findByEmail(loginRequest.getEmail())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            String accessToken = jwtService.generateAccessToken(user);
            String refreshToken = jwtService.generateRefreshToken(user);

            refreshTokenService.deleteByUser(user);
            refreshTokenService.createOrUpdateRefreshToken(
                    user,
                    refreshToken,
                    Instant.now().plusMillis(refreshExpiration)
            );

            setCookie(response, "accessToken", accessToken, 3600);
            setCookie(response, "refreshToken", refreshToken, (int) (refreshExpiration / 1000));

            return ResponseEntity.ok(Map.of(
                    "accessToken", accessToken,
                    "refreshToken", refreshToken,
                    "email", user.getEmail()
            ));
        } catch (BadCredentialsException ex) {
            log.error("Login failed for email: {}", loginRequest.getEmail());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Invalid credentials"));
        } catch (Exception ex) {
            log.error("Unexpected error during login: {}", ex.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Login failed"));
        }
    }

    @GetMapping("/oauth2/success")
    @Transactional
    public ResponseEntity<?> oauth2Success(@AuthenticationPrincipal OAuth2User oauth2User, HttpServletResponse response) {
        try {
            String email = oauth2User.getAttribute("email");

            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found after OAuth2 login"));

            String accessToken = jwtService.generateAccessToken(user);
            String refreshToken = jwtService.generateRefreshToken(user);

            refreshTokenService.deleteByUser(user);
            refreshTokenService.createOrUpdateRefreshToken(
                    user,
                    refreshToken,
                    Instant.now().plusMillis(refreshExpiration)
            );

            setCookie(response, "accessToken", accessToken, 3600);
            setCookie(response, "refreshToken", refreshToken, (int) (refreshExpiration / 1000));

            return ResponseEntity.ok(Map.of(
                    "accessToken", accessToken,
                    "refreshToken", refreshToken,
                    "email", user.getEmail(),
                    "name", user.getName()
            ));
        } catch (Exception e) {
            log.error("OAuth2 success handler failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "OAuth2 authentication failed"));
        }
    }

    @PostMapping("/oauth2/token")
    @Transactional
    public ResponseEntity<?> oauth2TokenLogin(@RequestBody OAuth2TokenRequest request, HttpServletResponse response) {
        try {
            User user = externalOAuth2Service.processOAuth2Token(request.getToken(), request.getProvider());

            String accessToken = jwtService.generateAccessToken(user);
            String refreshToken = jwtService.generateRefreshToken(user);

            refreshTokenService.deleteByUser(user);
            refreshTokenService.createOrUpdateRefreshToken(
                    user,
                    refreshToken,
                    Instant.now().plusMillis(refreshExpiration)
            );

            setCookie(response, "accessToken", accessToken, 3600);
            setCookie(response, "refreshToken", refreshToken, (int) (refreshExpiration / 1000));

            return ResponseEntity.ok(Map.of(
                    "email", user.getEmail(),
                    "name", user.getName(),
                    "accessToken", accessToken,
                    "refreshToken", refreshToken
            ));
        } catch (IllegalArgumentException e) {
            log.error("OAuth2 token login failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("OAuth2 token login failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "OAuth2 Login failed: " + e.getMessage()));
        }
    }

    @PostMapping("/logout")
    @Transactional
    public ResponseEntity<?> logout(
            @CookieValue(value = "refreshToken", required = false) String refreshToken,
            HttpServletResponse response
    ) {
        try {
            if (refreshToken != null) {
                refreshTokenRepository.deleteByToken(refreshToken);
            }

            clearCookie(response, "accessToken");
            clearCookie(response, "refreshToken");

            return ResponseEntity.ok(Map.of("message", "Logged out successfully"));
        } catch (Exception e) {
            log.error("Logout failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Logout failed"));
        }
    }

    private void setCookie(HttpServletResponse response, String name, String value, int maxAgeSeconds) {
        StringBuilder cookie = new StringBuilder()
                .append(name).append("=").append(value)
                .append("; Path=/")
                .append("; Max-Age=").append(maxAgeSeconds)
                .append("; HttpOnly");

        if (cookieSecure) {
            cookie.append("; Secure; SameSite=None");
        }

        if (cookieDomain != null && !cookieDomain.isEmpty()) {
            cookie.append("; Domain=").append(cookieDomain);
        }

        response.addHeader("Set-Cookie", cookie.toString());
    }

    private void clearCookie(HttpServletResponse response, String name) {
        StringBuilder cookie = new StringBuilder()
                .append(name).append("=")
                .append("; Path=/")
                .append("; Max-Age=0")
                .append("; HttpOnly");

        if (cookieSecure) {
            cookie.append("; Secure; SameSite=None");
        }

        if (cookieDomain != null && !cookieDomain.isEmpty()) {
            cookie.append("; Domain=").append(cookieDomain);
        }

        response.addHeader("Set-Cookie", cookie.toString());
    }

    @PostMapping("/refresh")
    @Transactional
    public ResponseEntity<?> refreshAccessToken(
            @RequestBody RefreshTokenRequest request,
            @CookieValue(value = "refreshToken", required = false) String refreshTokenCookie,
            HttpServletResponse response
    ) {
        try {
            if (refreshTokenCookie == null || request.getAccessToken() == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("error", "Missing tokens"));
            }

            RefreshToken savedToken = refreshTokenRepository.findByToken(refreshTokenCookie)
                    .orElseThrow(() -> new RuntimeException("Invalid refresh token"));

            if (savedToken.getExpiryDate().isBefore(Instant.now())) {
                refreshTokenRepository.delete(savedToken);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Refresh token expired"));
            }

            String userEmail = jwtService.extractUsername(request.getAccessToken());

            User user = userRepository.findByEmail(userEmail)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            String newAccessToken = jwtService.generateAccessToken(user);

            setCookie(response, "accessToken", newAccessToken, 3600);

            return ResponseEntity.ok(Map.of("accessToken", newAccessToken));
        } catch (Exception e) {
            log.error("Token refresh failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Token refresh failed: " + e.getMessage()));
        }
    }
}