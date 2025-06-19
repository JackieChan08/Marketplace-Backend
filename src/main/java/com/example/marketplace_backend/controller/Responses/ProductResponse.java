package com.example.marketplace_backend.controller.Responses;


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
    private String categoryName;
    private UUID categoryId;
    private List<FileResponse> images;
    private double price;
    private double discountedPrice;
    private UUID brandId;
}
