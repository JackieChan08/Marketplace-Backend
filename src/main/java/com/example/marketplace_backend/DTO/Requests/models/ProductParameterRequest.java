package com.example.marketplace_backend.DTO.Requests.models;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
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
    @NotBlank(message = "Название параметра не может быть пустым")
    private String name;

    @NotEmpty(message = "Список подпараметров не может быть пустым")
    @Valid
    private List<ProductSubParameterRequest> subParameters;
}