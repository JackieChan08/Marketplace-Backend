package com.example.marketplace_backend.controller.Requests.models;

import lombok.Data;

import java.util.UUID;

@Data
public class BrandRequest {
    private String name;
    private String image;
}
