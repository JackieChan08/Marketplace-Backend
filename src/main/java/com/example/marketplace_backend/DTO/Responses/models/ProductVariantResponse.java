package com.example.marketplace_backend.DTO.Responses.models;

import com.example.marketplace_backend.DTO.Responses.models.CartOrderFavorite.ColorResponseBasic;
import com.example.marketplace_backend.DTO.Responses.models.LaptopResponse.ChipResponse;
import com.example.marketplace_backend.DTO.Responses.models.PhoneSpecResponse.SimTypeResponse;
import com.example.marketplace_backend.DTO.Responses.models.TableResponse.TableModuleResponse;
import com.example.marketplace_backend.DTO.Responses.models.WatchResponse.StrapSizeResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductVariantResponse {
    private UUID id;
    private UUID productId;
    private String name;
    private Boolean availability;
    private ColorResponseBasic color;
    private List<ChipResponse> chipResponses;
    private List<SimTypeResponse> simTypeResponses;
    private List<TableModuleResponse> tableModuleResponses;
    private List<StrapSizeResponse> strapSizeResponses;
}
