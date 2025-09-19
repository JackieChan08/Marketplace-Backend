package com.example.marketplace_backend.DTO.Responses.models.TableResponse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TableModuleResponse {
    private UUID id;
    private String name;
    private List<TableMemoryResponse> tableMemoryResponses;
}