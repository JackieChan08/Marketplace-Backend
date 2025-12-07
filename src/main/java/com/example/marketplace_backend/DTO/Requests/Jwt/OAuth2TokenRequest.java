package com.example.marketplace_backend.DTO.Requests.Jwt;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class OAuth2TokenRequest {
    @NotBlank(message = "Token не может быть пустым")
    private String token;

    @NotBlank(message = "Provider обязателен")
    private String provider;
}