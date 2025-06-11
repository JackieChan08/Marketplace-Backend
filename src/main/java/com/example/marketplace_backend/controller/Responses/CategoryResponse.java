package com.example.marketplace_backend.controller.Responses;


import com.example.marketplace_backend.Model.FileEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CategoryResponse {
    private Long id;
    private String name;
    private String description;
    private FileResponse imageFile;
    private List<ProductResponse> products;
}
