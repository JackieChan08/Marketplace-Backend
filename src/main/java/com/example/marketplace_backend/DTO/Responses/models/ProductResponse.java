package com.example.marketplace_backend.DTO.Responses.models;


import com.example.marketplace_backend.Model.Description;
import com.example.marketplace_backend.Model.Statuses;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
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
    private BigDecimal price;
    private BigDecimal discountedPrice;
    private UUID brandId;
    private boolean availability;
    private UUID statusId;
}
