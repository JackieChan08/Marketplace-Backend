package com.example.marketplace_backend.DTO.Responses.models;

import com.example.marketplace_backend.enums.SimType;
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
public class PhoneSpecResponse {
    private UUID id;
    private String memory;
    private BigDecimal price;
    private SimType simType;
}
