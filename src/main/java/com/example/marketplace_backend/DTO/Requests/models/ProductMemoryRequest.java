package com.example.marketplace_backend.DTO.Requests.models;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class ProductMemoryRequest {
    @NotNull(message = "ID памяти обязателен")
    private UUID id;

    @NotBlank(message = "Значение памяти не может быть пустым")
    private String memory;
}