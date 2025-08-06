package com.example.marketplace_backend.DTO.Requests.Jwt;

import lombok.Data;

@Data
public class RefreshTokenRequest {
    private String accessToken;
}

