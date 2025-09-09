package com.example.marketplace_backend.DTO.Requests.models.WatchRequest;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WatchSpecRequest {
    private String title;
    private List<StrapSizeRequest> strapSizes;
}