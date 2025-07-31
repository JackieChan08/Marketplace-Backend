package com.example.marketplace_backend.DTO.Responses.models;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SubcategoryResponseSimple {
    private UUID id;
    private String name;
}

