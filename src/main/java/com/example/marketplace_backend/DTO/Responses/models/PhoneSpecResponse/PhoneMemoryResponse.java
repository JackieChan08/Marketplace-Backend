package com.example.marketplace_backend.DTO.Responses.models.PhoneSpecResponse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PhoneMemoryResponse {
    private UUID id;
    private String name;
    private BigDecimal price;
    private UUID productVariantId;
}
