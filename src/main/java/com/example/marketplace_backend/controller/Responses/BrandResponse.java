package com.example.marketplace_backend.controller.Responses;

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
public class BrandResponse {
    private UUID id;
    private String name;
    private List<FileResponse> images;
    private List<ProductResponse> products;
}
