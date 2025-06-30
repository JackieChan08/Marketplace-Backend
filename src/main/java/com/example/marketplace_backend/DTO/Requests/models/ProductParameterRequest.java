package com.example.marketplace_backend.DTO.Requests.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductParameterRequest {
    private String name;
    private List<ProductSubParameterRequest> subParameters;
}
