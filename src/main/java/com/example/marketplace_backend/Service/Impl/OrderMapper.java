package com.example.marketplace_backend.Service.Impl;

import com.example.marketplace_backend.DTO.Responses.models.OrderItemResponse;
import com.example.marketplace_backend.DTO.Responses.models.OrderResponse;
import com.example.marketplace_backend.DTO.Responses.models.OrderStatusResponse;
import com.example.marketplace_backend.DTO.Responses.models.OrderWholesaleResponse;
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

    public static OrderStatusResponse toOrderStatusResponse(com.example.marketplace_backend.Model.Statuses status) {
        if (status == null) {
            return null;
        }
        return OrderStatusResponse.builder()
                .id(status.getId())
                .name(status.getName())
                .primaryColor(status.getPrimaryColor())
                .backgroundColor(status.getBackgroundColor())
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
                .paymentMethod(order.getPaymentMethod())
                .orderNumber(order.getOrderNumber())
                .orderItems(itemResponses)
                .status(toOrderStatusResponse(order.getStatus()))
                .build();
    }

    public static OrderWholesaleResponse toOrderWholesaleResponse(Order order) {
        return OrderWholesaleResponse.builder()
                .id(order.getId())
                .address(order.getAddress())
                .phoneNumber(order.getPhoneNumber())
                .comment(order.getComment())
                .isWholesale(order.isWholesale())
                .createdAt(order.getCreatedAt())
                .userId(order.getUser().getId())
                .username(order.getUser().getName())
                .paymentMethod(order.getPaymentMethod())
                .orderNumber(order.getOrderNumber())
                .status(toOrderStatusResponse(order.getStatus()))
                .build();
    }
}