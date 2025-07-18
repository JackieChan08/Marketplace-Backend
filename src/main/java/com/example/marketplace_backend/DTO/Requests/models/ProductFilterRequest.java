package com.example.marketplace_backend.DTO.Requests.models;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
public class ProductFilterRequest {
    private List<UUID> subcategoryIds;
    private List<UUID> brandIds;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    private String sortBy;       // "price" или "name"
    private String sortDirection; // "asc" или "desc"
}

