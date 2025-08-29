package com.example.marketplace_backend.DTO.Requests.models;

import lombok.Data;

import java.util.UUID;

@Data
public class FavoriteRequest {
    private UUID productVariantId;
    private int quantity;
}