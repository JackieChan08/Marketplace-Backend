package com.example.marketplace_backend.controller.Responses;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductResponse {
    private Long id;
    private String name;
    private String description;
    private String categoryName;
    private Long categoryId;
    private List<FileResponse> images;
    private double price;
    private Long SubcategoryId;
}
