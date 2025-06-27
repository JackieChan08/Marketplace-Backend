package com.example.marketplace_backend.controller.Requests.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductSubParameterRequest {
    private String name;
    private String value;
}
