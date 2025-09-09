package com.example.marketplace_backend.DTO.Responses.models.ImageReponse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FileResponse {
    private UUID id;
    private String uniqueName;
    private String originalName;
    private String url;
    private String fileType;
}
