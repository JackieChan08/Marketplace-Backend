package com.example.marketplace_backend.DTO.Responses.models;

import com.example.marketplace_backend.DTO.Responses.models.LaptopResponse.LaptopSpecResponse;
import com.example.marketplace_backend.DTO.Responses.models.TableResponse.TableSpecResponse;
import com.example.marketplace_backend.DTO.Responses.models.WatchResponse.WatchSpecResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VariantResponse {
    private UUID id;
    private ColorResponse color;
    private PhoneSpecResponse phoneSpec;
    private LaptopSpecResponse laptopSpec;
    private TableSpecResponse tableSpec;
    private WatchSpecResponse watchSpec;
}
