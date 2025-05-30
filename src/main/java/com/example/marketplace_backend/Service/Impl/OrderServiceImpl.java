package com.example.marketplace_backend.Service.Impl;

import com.example.marketplace_backend.Model.*;
import com.example.marketplace_backend.Repositories.*;
import org.springframework.stereotype.Service;

import java.util.List;
@Service

public class OrderServiceImpl extends BaseServiceImpl<Order, Long> {
    private final OrderRepository orderRepository;

    public OrderServiceImpl(OrderRepository orderRepository) {
        super(orderRepository);
        this.orderRepository = orderRepository;
    }

    public List<Order> getAllOrdersIfActive() {
        return orderRepository.getAllOrdersIfActive();
    }
}
