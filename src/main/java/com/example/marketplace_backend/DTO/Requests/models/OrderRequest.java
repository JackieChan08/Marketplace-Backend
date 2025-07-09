package com.example.marketplace_backend.DTO.Requests.models;


import jakarta.persistence.Column;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class OrderRequest {
    private String address;
    private String phoneNumber;
    private Boolean isWholesale;
    private String comment;
    private List<UUID> cartItemIds;
}
