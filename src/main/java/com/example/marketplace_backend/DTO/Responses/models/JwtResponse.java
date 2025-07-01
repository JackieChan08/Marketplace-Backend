package com.example.marketplace_backend.DTO.Responses.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class JwtResponse {
    private final String type = "Bearer";
    private String accessToken;
}
