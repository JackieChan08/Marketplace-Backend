package com.example.marketplace_backend.Service.Impl;

import com.example.marketplace_backend.DTO.Requests.models.OrderStatusRequest;
import com.example.marketplace_backend.Model.OrderStatuses;
import com.example.marketplace_backend.Repositories.OrderStatusRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class OrderStatusServiceImpl extends BaseServiceImpl{
    public final OrderStatusRepository orderStatusRepository;

    public OrderStatusServiceImpl(OrderStatusRepository orderStatusesRepository) {
        super(orderStatusesRepository);
        this.orderStatusRepository = orderStatusesRepository;
    }

    public List<OrderStatuses> getAllOrderStatuses() {
        return orderStatusRepository.findAll();
    }

    public Optional<OrderStatuses> getOrderStatusById(UUID id) {
        return orderStatusRepository.findById(id);
    }

    public Optional<OrderStatuses> findByName(String name) {
        return orderStatusRepository.findByName(name);
    }

    public OrderStatuses createOrderStatus(OrderStatusRequest request) {
        if (existsByName(request.getName())) {
            throw new RuntimeException("Order status with name '" + request.getName() + "' already exists");
        }

        OrderStatuses orderStatuses = OrderStatuses.builder()
                .name(request.getName())
                .color(request.getColor())
                .build();

        return orderStatusRepository.save(orderStatuses);
    }

    public OrderStatuses updateOrderStatus(UUID id, OrderStatusRequest request) {
        OrderStatuses orderStatus = orderStatusRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order status not found with id: " + id));

        Optional<OrderStatuses> existingStatus = findByName(request.getName());
        if (existingStatus.isPresent() && !existingStatus.get().getId().equals(id)) {
            throw new RuntimeException("Order status with name '" + request.getName() + "' already exists");
        }

        orderStatus.setName(request.getName());
        orderStatus.setColor(request.getColor());

        return orderStatusRepository.save(orderStatus);
    }

    public void deleteOrderStatus(UUID id) {
        OrderStatuses orderStatus = orderStatusRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order status not found with id: " + id));

        if (orderStatus.getOrders() != null && !orderStatus.getOrders().isEmpty()) {
            throw new RuntimeException("Cannot delete order status that is being used by orders");
        }

        orderStatusRepository.delete(orderStatus);
    }

    public boolean existsByName(String name) {
        return orderStatusRepository.existsByName(name);
    }


}
