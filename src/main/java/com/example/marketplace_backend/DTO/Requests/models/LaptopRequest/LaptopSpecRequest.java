package com.example.marketplace_backend.DTO.Requests.models.LaptopRequest;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LaptopSpecRequest {
    private String title;
    private List<ChipRequest> chips;
}
