package com.example.marketplace_backend.controller.Requests.Jwt;

import lombok.Data;

@Data
public class RegisterRequest {
    private String name;
    private String email;
    private String password;

}

