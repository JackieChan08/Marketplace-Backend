package com.example.marketplace_backend.DTO.Responses.models;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PhoneConnectionResponse {
    private UUID id;
    private String simType;
}
