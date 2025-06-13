package com.example.marketplace_backend.controller.Requests.models;


import lombok.Data;

@Data
public class CartRequest {
    private Long productId;
    private int quantity;
}

