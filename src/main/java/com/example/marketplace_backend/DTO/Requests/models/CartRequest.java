package com.example.marketplace_backend.DTO.Requests.models;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class CartRequest {
    @NotNull(message = "ID варианта продукта обязателен")
    private UUID productVariantId;

    @Min(value = 1, message = "Количество должно быть не менее 1")
    private int quantity;
}