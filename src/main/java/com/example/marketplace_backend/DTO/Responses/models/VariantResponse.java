package com.example.marketplace_backend.DTO.Responses.models;

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
public class VariantResponse {
    private UUID id;
    private ColorResponse color;
    private PhoneSpecResponse phoneSpec;
    private LaptopSpecResponse laptopSpec;
}
