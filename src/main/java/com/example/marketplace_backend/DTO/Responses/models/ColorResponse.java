package com.example.marketplace_backend.DTO.Responses.models;

import com.example.marketplace_backend.DTO.Responses.models.ImageReponse.FileResponse;
import com.example.marketplace_backend.DTO.Responses.models.LaptopResponse.ChipResponse;
import com.example.marketplace_backend.DTO.Responses.models.PhoneSpecResponse.SimTypeResponse;
import com.example.marketplace_backend.DTO.Responses.models.TableResponse.TableModuleResponse;
import com.example.marketplace_backend.DTO.Responses.models.WatchResponse.StrapSizeResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ColorResponse {
    private UUID id;
    private String name;
    private String hex;
    private List<FileResponse> images;
    private List<ChipResponse> chipResponses;
    private List<SimTypeResponse> simTypeResponses;
    private List<TableModuleResponse> tableModuleResponses;
    private List<StrapSizeResponse> strapSizeResponses;
}


