package com.example.marketplace_backend.DTO.Responses.models;

import com.example.marketplace_backend.DTO.Responses.models.ImageReponse.FileResponse;
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
public class ColorResponse {
    private UUID id;
    private String name;
    private String hex;
    private BigDecimal price;
    private List<FileResponse> images;
}

