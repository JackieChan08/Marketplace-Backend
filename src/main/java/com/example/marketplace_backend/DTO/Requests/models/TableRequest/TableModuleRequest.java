package com.example.marketplace_backend.DTO.Requests.models.TableRequest;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TableModuleRequest {
    private String name;
    private List<TableMemoryRequest> memories;
}