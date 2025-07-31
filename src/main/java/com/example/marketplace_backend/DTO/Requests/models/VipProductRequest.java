package com.example.marketplace_backend.DTO.Requests.models;

import com.example.marketplace_backend.Model.FileEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VipProductRequest {
    private UUID id;
    private String name;
    private MultipartFile image;
}
