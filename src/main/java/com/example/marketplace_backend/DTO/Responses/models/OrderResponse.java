package com.example.marketplace_backend.DTO.Responses.models;

import com.example.marketplace_backend.Model.Enums.PaymentMethod;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderResponse {
    private UUID id;
    private String address;
    private String phoneNumber;
    private String comment;
    private BigDecimal totalPrice;
    private int totalQuantity;
    private boolean isWholesale;
    private LocalDateTime createdAt;
    private PaymentMethod paymentMethod;
    private String orderNumber;

    private UUID userId;
    private String username;

    private List<OrderItemResponse> orderItems;
    private OrderStatusResponse status;
}
