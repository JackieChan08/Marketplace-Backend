package com.example.marketplace_backend.DTO.Responses.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CategoryWithSubcategoryResponse {
    private UUID id;
    private String name;

    private List<SubcategoryResponseSimple> subcategoryResponsesSimple;
}