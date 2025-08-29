package com.example.marketplace_backend.DTO.Responses.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    private LaptopSpecResponse laptopSpec;
}
