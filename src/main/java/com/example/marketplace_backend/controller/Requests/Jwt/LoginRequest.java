package com.example.marketplace_backend.controller.Requests.Jwt;


import lombok.Data;

@Data
public class LoginRequest {
    private String email;
    private String password;
}
