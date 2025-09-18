package com.example.marketplace_backend.DTO.Requests.models.LaptopRequest;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RamRequest {
    private String name;
    private BigDecimal price;
}
