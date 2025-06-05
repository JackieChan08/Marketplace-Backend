package com.example.marketplace_backend.controller.Requests.Jwt;

import lombok.Data;

@Data
public class JwtRequest {
    private String login;
    private String password;
}
