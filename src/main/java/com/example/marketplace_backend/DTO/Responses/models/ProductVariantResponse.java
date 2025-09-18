package com.example.marketplace_backend.DTO.Responses.models;

import com.example.marketplace_backend.DTO.Responses.models.LaptopResponse.ChipResponse;
import com.example.marketplace_backend.DTO.Responses.models.TableResponse.TableSpecResponse;
import com.example.marketplace_backend.DTO.Responses.models.WatchResponse.WatchSpecResponse;
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
    private ColorResponse color;
    private PhoneSpecResponse phoneSpec;
    private List<ChipResponse> chipResponses;
    private TableSpecResponse tableSpec;
    private WatchSpecResponse watchSpec;
}
