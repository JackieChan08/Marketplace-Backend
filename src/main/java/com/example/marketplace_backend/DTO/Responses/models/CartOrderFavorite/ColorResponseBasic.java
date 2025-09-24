package com.example.marketplace_backend.DTO.Responses.models.CartOrderFavorite;

import com.example.marketplace_backend.DTO.Responses.models.ImageReponse.FileResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ColorResponseBasic {
    private UUID id;
    private String name;
    private String hex;
    private List<FileResponse> images;
}


