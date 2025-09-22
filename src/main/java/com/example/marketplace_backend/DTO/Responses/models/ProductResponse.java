package com.example.marketplace_backend.DTO.Responses.models;


import com.example.marketplace_backend.DTO.Responses.models.ImageReponse.FileResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductResponse {
    private UUID id;
    private String name;
    private BigDecimal price;
    private String priceDescription;
    private BigDecimal discountedPrice;
    private boolean availability;
    private String title;
    private String description;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;

    private String subcategoryName;
    private UUID subcategoryId;

    private UUID brandId;
    private String brandName;
    private String categoryName;
    private UUID categoryId;

    private List<FileResponse> images;
    private List<StatusResponse> statuses;
    private List<ProductParameterResponse> parameters;
    private List<ColorResponse> colors;
    private ProductVariantResponse singleVariant;
}

