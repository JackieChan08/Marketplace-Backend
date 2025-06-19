package com.example.marketplace_backend.controller.Requests.models;


import lombok.Data;

import java.util.UUID;

@Data
public class CartRequest {
    private UUID productId;
    private int quantity;
}

