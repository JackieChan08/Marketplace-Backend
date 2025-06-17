package com.example.marketplace_backend.controller.Requests.models;


import lombok.Data;

@Data
public class OrderRequest {
    private String address;
    private String phoneNumber;
}
