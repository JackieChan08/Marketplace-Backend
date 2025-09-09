package com.example.marketplace_backend.DTO.Responses.models.WatchResponse;

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
public class WatchSpecResponse {
    private UUID id;
    private String title;
    private List<StrapSizeResponse> strapSizes;
}