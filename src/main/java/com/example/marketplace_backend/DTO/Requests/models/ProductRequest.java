package com.example.marketplace_backend.DTO.Requests.models;

import com.example.marketplace_backend.Model.Description;
import com.example.marketplace_backend.Model.Statuses;
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
    private String name;
    private BigDecimal price;
    private UUID subCategoryId;
    private UUID brandId;
    private boolean availability;
    private String title;
    private String description;

    private List<MultipartFile> images;
    private List<UUID> statusId;
}
