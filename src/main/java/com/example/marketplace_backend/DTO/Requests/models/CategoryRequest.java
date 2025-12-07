package com.example.marketplace_backend.DTO.Requests.models;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CategoryRequest {
    @NotBlank(message = "Название категории не может быть пустым")
    private String name;

    @NotNull(message = "Изображение обязательно")
    private MultipartFile image;

    @NotNull(message = "Иконка обязательна")
    private MultipartFile icon;

    @NotNull(message = "Приоритет обязателен")
    private boolean priority;
}