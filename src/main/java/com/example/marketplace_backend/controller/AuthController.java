package com.example.marketplace_backend.controller;


import com.example.marketplace_backend.Model.RefreshToken;
import com.example.marketplace_backend.Model.User;
import com.example.marketplace_backend.Repositories.RefreshTokenRepository;
import com.example.marketplace_backend.Repositories.UserRepository;
import com.example.marketplace_backend.Service.Impl.JwtService;
import com.example.marketplace_backend.Service.Impl.UserServiceImpl;
import com.example.marketplace_backend.controller.Requests.Jwt.LoginRequest;
import com.example.marketplace_backend.controller.Requests.Jwt.RegisterRequest;
import com.example.marketplace_backend.controller.Responses.Jwt.JwtResponse;
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


    @PostMapping("/register")
    public ResponseEntity<JwtResponse> register(@RequestBody RegisterRequest request) {
        JwtResponse jwtResponse = userService.register(request);
        return ResponseEntity.ok(jwtResponse);
    }



    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
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
    public ResponseEntity<?> oauth2Success(@AuthenticationPrincipal OAuth2User oauth2User) {
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

        return ResponseEntity.ok(Map.of(
                "accessToken", accessToken,
                "refreshToken", refreshToken,
                "email", user.getEmail(),
                "name", user.getName()
        ));
    }

}
