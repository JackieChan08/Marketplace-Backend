package com.example.marketplace_backend.controller.Responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FileResponse {
    private String uniqueName;
    private String originalName;
    private String url;
    private String fileType;
}
