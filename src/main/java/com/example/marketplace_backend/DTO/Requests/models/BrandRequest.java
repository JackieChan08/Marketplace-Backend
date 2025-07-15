package com.example.marketplace_backend.DTO.Requests.models;

import com.example.marketplace_backend.Model.FileEntity;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Data
public class BrandRequest {
    private String name;
    private MultipartFile image;
}
