package com.example.marketplace_backend.DTO.Responses;


import com.example.marketplace_backend.Model.Description;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductResponse {
    private UUID id;
    private String name;
    private List<Description> descriptions;
    private UUID categoryId;
    private String categoryName;
    private String subcategoryName;
    private UUID subcategoryId;
    private List<FileResponse> images;
    private double price;
    private double discountedPrice;
    private UUID brandId;
}
