package com.example.marketplace_backend.DTO.Requests.models;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SubcategoryRequest {
    @NotBlank(message = "Название подкатегории не может быть пустым")
    private String name;

    @NotNull(message = "ID категории обязателен")
    private UUID categoryId;
}