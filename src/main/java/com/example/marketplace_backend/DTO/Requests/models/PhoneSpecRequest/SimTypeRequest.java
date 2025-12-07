package com.example.marketplace_backend.DTO.Requests.models.PhoneSpecRequest;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SimTypeRequest {
    @NotBlank(message = "Название типа SIM не может быть пустым")
    private String name;

    @NotEmpty(message = "Список памяти не может быть пустым")
    @Valid
    private List<PhoneMemoryRequest> phoneMemoryRequests;
}