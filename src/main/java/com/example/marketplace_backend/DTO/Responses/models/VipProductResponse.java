package com.example.marketplace_backend.DTO.Responses.models;

import com.example.marketplace_backend.Model.FileEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VipProductResponse {
    private UUID id;
    private String name;
    private FileEntity image;
}
