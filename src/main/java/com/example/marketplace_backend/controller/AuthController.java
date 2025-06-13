package com.example.marketplace_backend.controller;


import com.example.marketplace_backend.Model.RefreshToken;
import com.example.marketplace_backend.Model.User;
import com.example.marketplace_backend.Repositories.RefreshTokenRepository;
import com.example.marketplace_backend.Repositories.UserRepository;
import com.example.marketplace_backend.Service.Impl.ExternalOAuth2ServiceImpl;
import com.example.marketplace_backend.Service.Impl.JwtService;
import com.example.marketplace_backend.Service.Impl.UserServiceImpl;
import com.example.marketplace_backend.controller.Requests.Jwt.LoginRequest;
import com.example.marketplace_backend.controller.Requests.Jwt.OAuth2TokenRequest;
import com.example.marketplace_backend.controller.Requests.Jwt.RegisterRequest;
import com.example.marketplace_backend.controller.Responses.Jwt.JwtResponse;
import jakarta.servlet.http.Cookie;
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

            Cookie accessCookie = new Cookie("accessToken", accessToken);
            accessCookie.setHttpOnly(true);
            accessCookie.setPath("/");
            accessCookie.setMaxAge(3600);

            Cookie refreshCookie = new Cookie("refreshToken", refreshToken);
            refreshCookie.setHttpOnly(true);
            refreshCookie.setPath("/");
            refreshCookie.setMaxAge(2592000);

            response.addCookie(accessCookie);
            response.addCookie(refreshCookie);

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

        Cookie accessCookie = new Cookie("accessToken", accessToken);
        accessCookie.setHttpOnly(true);
        accessCookie.setPath("/");
        accessCookie.setMaxAge(3600);

        Cookie refreshCookie = new Cookie("refreshToken", refreshToken);
        refreshCookie.setHttpOnly(true);
        refreshCookie.setPath("/");
        refreshCookie.setMaxAge(2592000);

        response.addCookie(accessCookie);
        response.addCookie(refreshCookie);

        return ResponseEntity.ok(Map.of(
                "accessToken", accessToken,
                "refreshToken", refreshToken,
                "email", user.getEmail(),
                "name", user.getName()
        ));
    }
    @PostMapping("/oauth2/token")
    public ResponseEntity<?> oauth2TokenLogin(
            @RequestBody OAuth2TokenRequest request,
            HttpServletResponse response
    ) {
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

            Cookie accessCookie = new Cookie("accessToken", accessToken);
            accessCookie.setHttpOnly(true);
            accessCookie.setPath("/");
            accessCookie.setMaxAge(3600);

            Cookie refreshCookie = new Cookie("refreshToken", refreshToken);
            refreshCookie.setHttpOnly(true);
            refreshCookie.setPath("/");
            refreshCookie.setMaxAge(2592000);

            response.addCookie(accessCookie);
            response.addCookie(refreshCookie);

            return ResponseEntity.ok(Map.of(
                    "email", user.getEmail(),
                    "name", user.getName()
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

        clearCookie("accessToken", response);
        clearCookie("refreshToken", response);

        return ResponseEntity.ok("Logged out successfully");
    }

    private void clearCookie(String name, HttpServletResponse response) {
        Cookie cookie = new Cookie(name, null);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }


}
