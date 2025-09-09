package com.example.marketplace_backend.DTO.Responses.models.LaptopResponse;

import com.example.marketplace_backend.Model.ProductSpec.LaptopSpec.Chip;
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
public class ChipResponse {
    private UUID id;
    private String name;
    private List<SsdResponse> ssdResponses;
}
