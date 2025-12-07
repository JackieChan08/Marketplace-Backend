package com.example.marketplace_backend.DTO.Requests.models;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StatusRequest {
    @NotBlank(message = "Название статуса не может быть пустым")
    private String name;

    @NotBlank(message = "Основной цвет обязателен")
    @Pattern(regexp = "^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$", message = "Некорректный HEX код цвета")
    private String primaryColor;

    @NotBlank(message = "Цвет фона обязателен")
    @Pattern(regexp = "^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$", message = "Некорректный HEX код цвета")
    private String backgroundColor;

    @NotNull(message = "Флаг заказа обязателен")
    private boolean orderFlag;

    @NotNull(message = "Флаг продукта обязателен")
    private boolean productFlag;
}