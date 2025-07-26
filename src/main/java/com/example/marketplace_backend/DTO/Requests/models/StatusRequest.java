package com.example.marketplace_backend.DTO.Requests.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StatusRequest {
    private String name;
    private String primaryColor;
    private String backgroundColor;
    private boolean orderFlag;
    private boolean productFlag;
}
