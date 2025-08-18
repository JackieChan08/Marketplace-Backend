package com.example.marketplace_backend.DTO.Requests.models;

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
public class ColorRequest {
    private String name;
    private String hex;

    private List<MultipartFile> images;
    private List<UUID> connectionIds;
    private List<MemoryWithPriceRequest> memories;
}