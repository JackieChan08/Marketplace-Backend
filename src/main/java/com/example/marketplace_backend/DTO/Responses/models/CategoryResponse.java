package com.example.marketplace_backend.DTO.Responses.models;


import com.example.marketplace_backend.DTO.Responses.models.ImageReponse.CategoryIconResponse;
import com.example.marketplace_backend.DTO.Responses.models.ImageReponse.CategoryImageResponse;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryResponse {
    private UUID id;
    private String name;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;
    private boolean priority;
    private CategoryImageResponse categoryImage;
    private CategoryIconResponse categoryIcon;

    @JsonProperty("subcategories")
    private List<SubcategoryResponseSimple> subcategoryResponsesSimple;
}
