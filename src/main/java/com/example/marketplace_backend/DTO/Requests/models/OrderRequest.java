package com.example.marketplace_backend.DTO.Requests.models;


import jakarta.persistence.Column;
import lombok.Data;

@Data
public class OrderRequest {
    private String address;
    private String phoneNumber;
    private Boolean isWholesale;
    private String comment;
}
