package com.example.marketplace_backend.DTO.Requests.Jwt;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RefreshTokenRequest {
    @NotBlank(message = "Access token не может быть пустым")
    private String accessToken;
}

