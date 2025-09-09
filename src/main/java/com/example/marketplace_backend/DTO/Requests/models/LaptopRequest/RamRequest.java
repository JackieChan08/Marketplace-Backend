package com.example.marketplace_backend.DTO.Requests.models.LaptopRequest;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RamRequest {
    private String name;
    private String price;
}
