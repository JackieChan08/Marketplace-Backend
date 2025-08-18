package com.example.marketplace_backend.DTO.Requests.models;


import com.example.marketplace_backend.enums.PaymentMethod;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class OrderRequest {
    private UUID orderId;
    private String address;
    private String phoneNumber;
    private Boolean isWholesale;
    private String comment;
    private PaymentMethod paymentMethod;

    private UUID statusId;
    private List<UUID> cartItemIds;
}
