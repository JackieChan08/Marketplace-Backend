package com.example.marketplace_backend.DTO.Requests.models;

import lombok.Data;

import java.util.UUID;

@Data
public class StatusRequest {
    private String name;
    private String color;
}
