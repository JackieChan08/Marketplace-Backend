package com.example.marketplace_backend.DTO.Requests.models;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
public class ProductMemoryRequest {
    private UUID id;
    private String memory;
}
