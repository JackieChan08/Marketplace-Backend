package com.example.marketplace_backend.DTO.Responses.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FavoriteItemResponse {
    private UUID favoriteItemId;
    private UUID userId;
    private int quantity;
    private ProductVariantResponse productVariant;
    private BigDecimal pricePerItem;
    private BigDecimal totalPrice;
    private LocalDateTime addedAt;
}