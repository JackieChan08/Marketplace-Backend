package com.example.marketplace_backend.DTO.Requests.models.WatchRequest;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DialRequest {
    private String size_mm;
    private BigDecimal price;
}