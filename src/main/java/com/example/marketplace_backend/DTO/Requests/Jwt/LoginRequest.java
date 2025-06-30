package com.example.marketplace_backend.DTO.Requests.Jwt;


import lombok.Data;

@Data
public class LoginRequest {
    private String email;
    private String password;
}
