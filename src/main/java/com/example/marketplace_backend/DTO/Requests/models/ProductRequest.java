package com.example.marketplace_backend.DTO.Requests.models;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductRequest {
    @NotBlank(message = "Название продукта не может быть пустым")
    private String name;

    @NotNull(message = "Цена обязательна")
    @DecimalMin(value = "0.0", inclusive = false, message = "Цена должна быть больше 0")
    private BigDecimal price;

    private String priceDescription;

    @NotNull(message = "ID подкатегории обязателен")
    private UUID subCategoryId;

    @NotNull(message = "ID бренда обязателен")
    private UUID brandId;

    @NotNull(message = "Необходимо указать доступность товара")
    private Boolean availability;

    @NotBlank(message = "Заголовок не может быть пустым")
    private String title;

    @NotBlank(message = "Описание не может быть пустым")
    private String description;

    private String parametersJson;

    @NotEmpty(message = "Необходимо загрузить хотя бы одно изображение")
    private List<MultipartFile> images;

    @NotEmpty(message = "Необходимо указать хотя бы один статус")
    private List<UUID> statusId;

    private List<ColorWithSpecsRequest> colors;
}