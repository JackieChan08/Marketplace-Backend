package com.example.marketplace_backend.controller.Responses.Jwt;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class JwtResponse  {
    private String accessToken;
    private String refreshToken;
}