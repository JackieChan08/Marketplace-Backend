package com.example.marketplace_backend.DTO.Requests.models;

import com.example.marketplace_backend.enums.SimType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PhoneSpecRequest {
    private String memory;
    private BigDecimal price;
    private SimType simType;
}
