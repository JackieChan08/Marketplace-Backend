package com.example.marketplace_backend.DTO.Responses.models.LaptopResponse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RamResponse {
    private UUID id;
    private String name;
    private BigDecimal price;
}
