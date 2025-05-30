package com.example.marketplace_backend.controller.Requests.Jwt;

import lombok.Data;

@Data
public class AccessRequest {
    private String userLogin;
    private String accessToken;
}
