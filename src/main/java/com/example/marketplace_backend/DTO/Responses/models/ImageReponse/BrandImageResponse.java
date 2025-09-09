package com.example.marketplace_backend.DTO.Responses.models.ImageReponse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BrandImageResponse {
    private UUID id;
    private FileResponse image;
}


