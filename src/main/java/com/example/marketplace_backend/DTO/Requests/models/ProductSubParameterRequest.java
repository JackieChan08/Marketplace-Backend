package com.example.marketplace_backend.DTO.Requests.models;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductSubParameterRequest {
    @NotBlank(message = "Название подпараметра не может быть пустым")
    private String name;

    @NotBlank(message = "Значение подпараметра не может быть пустым")
    private String value;
}