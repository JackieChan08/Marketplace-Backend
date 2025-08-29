package com.example.marketplace_backend.DTO.Requests.models;

import java.util.List;

import lombok.*;
import java.math.BigDecimal;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VariantRequest {
    private ColorRequest color;
    private PhoneSpecRequest phoneSpec;
    private LaptopSpecRequest laptopSpec;
}

