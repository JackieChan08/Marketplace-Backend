package com.example.marketplace_backend.controller.Requests.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor

public class ProductRequest {
    private String name;
    private String description;
    private double price;
    private String image;
    private Long categoryId;
}
