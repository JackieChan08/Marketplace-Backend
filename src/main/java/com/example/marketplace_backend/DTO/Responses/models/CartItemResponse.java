package com.example.marketplace_backend.DTO.Responses.models;


import lombok.AllArgsConstructor;
import lombok.Data;

import lombok.Builder;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CartItemResponse {
    private UUID cartItemId;
    private UUID userId;
    private ProductResponse productResponse;
    private int quantity;
    private BigDecimal pricePerItem;
    private BigDecimal totalPrice;
    private LocalDateTime addedAt;
}
