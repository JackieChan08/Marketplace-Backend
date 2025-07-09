package com.example.marketplace_backend.DTO.Requests.models;


import com.example.marketplace_backend.Model.Statuses;
import jakarta.persistence.Column;
import lombok.Data;

import java.util.List;

@Data
public class OrderRequest {
    private String address;
    private String phoneNumber;
    private Boolean isWholesale;
    private String comment;
    private List<Statuses> statuses;
}
