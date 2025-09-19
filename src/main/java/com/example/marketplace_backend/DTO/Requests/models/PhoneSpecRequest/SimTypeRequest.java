package com.example.marketplace_backend.DTO.Requests.models.PhoneSpecRequest;

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
    private String name;
    private List<PhoneMemoryRequest> phoneMemoryRequests;
}
