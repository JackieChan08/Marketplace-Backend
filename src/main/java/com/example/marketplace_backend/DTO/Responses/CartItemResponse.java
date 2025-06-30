package com.example.marketplace_backend.DTO.Responses;


import lombok.AllArgsConstructor;
import lombok.Data;

import lombok.Builder;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CartItemResponse {
    private UUID productId;
    private String productName;
    private int quantity;
    private BigDecimal pricePerItem;
    private BigDecimal totalPrice;
}
