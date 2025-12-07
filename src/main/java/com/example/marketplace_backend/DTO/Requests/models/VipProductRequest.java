package com.example.marketplace_backend.DTO.Requests.models;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VipProductRequest {
    private UUID id;

    @NotBlank(message = "Название VIP продукта не может быть пустым")
    private String name;

    @NotNull(message = "Изображение обязательно")
    private MultipartFile image;
}