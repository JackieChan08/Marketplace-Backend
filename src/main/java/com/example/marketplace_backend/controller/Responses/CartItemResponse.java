package com.example.marketplace_backend.controller.Responses;


import lombok.AllArgsConstructor;
import lombok.Data;

import lombok.Builder;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CartItemResponse {
    private Long productId;
    private String productName;
    private int quantity;
    private double pricePerItem;
    private double totalPrice;
}
