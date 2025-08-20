package com.example.marketplace_backend.DTO.Responses.models;

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
public class ColorResponse {
    private UUID id;
    private String name;
    private String hex;
    private List<FileResponse> images;

    private List<PhoneSpecResponse> phoneSpecs;
    private List<LaptopSpecResponse> laptopSpecs;
}

