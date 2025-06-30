package com.example.marketplace_backend.DTO.Requests.Jwt;

import lombok.Data;


@Data
public class OAuth2TokenRequest {
    private String token;
    private String provider;
}
