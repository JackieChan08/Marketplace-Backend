package com.example.marketplace_backend.controller.Requests;

import lombok.Data;

@Data
public class JwtRequest {
    private String login;
    private String password;
}
