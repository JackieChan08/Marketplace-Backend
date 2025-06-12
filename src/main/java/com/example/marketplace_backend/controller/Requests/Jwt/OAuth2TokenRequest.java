package com.example.marketplace_backend.controller.Requests.Jwt;

import lombok.Data;


@Data
public class OAuth2TokenRequest {
    private String token;
    private String provider;
}
