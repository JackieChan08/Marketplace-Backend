package com.example.marketplace_backend.controller.Requests.models;

import com.example.marketplace_backend.Model.Description;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductRequest {
    private String name;
    private double price;
    private UUID categoryId;
    private List<MultipartFile> images;
    private List<Description> descriptions;
    private UUID brandId;
}
