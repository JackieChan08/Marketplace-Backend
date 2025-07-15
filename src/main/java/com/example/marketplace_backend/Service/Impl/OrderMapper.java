package com.example.marketplace_backend.Service.Impl;


import com.example.marketplace_backend.DTO.Responses.models.OrderItemResponse;
import com.example.marketplace_backend.DTO.Responses.models.OrderResponse;
import com.example.marketplace_backend.Model.Intermediate_objects.OrderItem;
import com.example.marketplace_backend.Model.Order;

import java.util.List;
import java.util.stream.Collectors;

public class OrderMapper {

    public static OrderItemResponse toOrderItemResponse(OrderItem item) {
        return OrderItemResponse.builder()
                .id(item.getId())
                .productId(item.getProduct().getId())
                .productName(item.getProduct().getName())
                .quantity(item.getQuantity())
                .price(item.getPrice())
                .build();
    }

    public static OrderResponse toOrderResponse(Order order) {
        List<OrderItemResponse> itemResponses = order.getOrderItems().stream()
                .map(OrderMapper::toOrderItemResponse)
                .collect(Collectors.toList());

        return OrderResponse.builder()
                .id(order.getId())
                .address(order.getAddress())
                .phoneNumber(order.getPhoneNumber())
                .comment(order.getComment())
                .totalPrice(order.getTotalPrice())
                .isWholesale(order.isWholesale())
                .createdAt(order.getCreatedAt())
                .userId(order.getUser().getId())
                .username(order.getUser().getName())
                .orderItems(itemResponses)
                .build();
    }
}
