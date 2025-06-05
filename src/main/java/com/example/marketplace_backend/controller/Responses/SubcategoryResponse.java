package com.example.marketplace_backend.controller.Responses;


import lombok.Data;

@Data
public class SubcategoryResponse {
    private Long id;
    private String name;
    private String description;
    private Long categoryId;
    private String categoryName;
    private Long parentSubcategoryId;
    private String parentSubcategoryName;
    private FileResponse image;
}

