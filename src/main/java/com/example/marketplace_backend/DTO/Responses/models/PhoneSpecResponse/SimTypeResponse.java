package com.example.marketplace_backend.DTO.Responses.models.PhoneSpecResponse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SimTypeResponse {
    private UUID id;
    private String name;
    private List<PhoneMemoryResponse> phoneMemoryResponses;
}
