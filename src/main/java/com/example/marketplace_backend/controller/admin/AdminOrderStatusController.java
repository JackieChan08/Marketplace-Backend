package com.example.marketplace_backend.controller.admin;

import com.example.marketplace_backend.Service.Impl.OrderStatusServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.marketplace_backend.Model.OrderStatuses;
import com.example.marketplace_backend.DTO.Requests.models.OrderStatusRequest;
import com.example.marketplace_backend.DTO.Responses.models.OrderStatusResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/admin/order-status")
public class AdminOrderStatusController {
    private final OrderStatusServiceImpl orderStatusService;

    @PostMapping
    public ResponseEntity<OrderStatusResponse> createOrderStatus(@Valid @RequestBody OrderStatusRequest request) {
        try {
            OrderStatuses orderStatus = orderStatusService.createOrderStatus(request);
            OrderStatusResponse response = OrderStatusResponse.builder()
                    .id(orderStatus.getId())
                    .name(orderStatus.getName())
                    .color(orderStatus.getColor())
                    .build();
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @GetMapping
    public ResponseEntity<List<OrderStatusResponse>> getAllOrderStatuses() {
        List<OrderStatuses> orderStatuses = orderStatusService.getAllOrderStatuses();
        List<OrderStatusResponse> responses = orderStatuses.stream()
                .map(orderStatus -> OrderStatusResponse.builder()
                        .id(orderStatus.getId())
                        .name(orderStatus.getName())
                        .color(orderStatus.getColor())
                        .build())
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderStatusResponse> getOrderStatusById(@PathVariable UUID id) {
        return orderStatusService.getOrderStatusById(id)
                .map(orderStatus -> OrderStatusResponse.builder()
                        .id(orderStatus.getId())
                        .name(orderStatus.getName())
                        .color(orderStatus.getColor())
                        .build())
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<OrderStatusResponse> updateOrderStatus(
            @PathVariable UUID id,
            @Valid @RequestBody OrderStatusRequest request) {
        try {
            OrderStatuses orderStatus = orderStatusService.updateOrderStatus(id, request);
            OrderStatusResponse response = OrderStatusResponse.builder()
                    .id(orderStatus.getId())
                    .name(orderStatus.getName())
                    .color(orderStatus.getColor())
                    .build();
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrderStatus(@PathVariable UUID id) {
        try {
            orderStatusService.deleteOrderStatus(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @GetMapping("/search")
    public ResponseEntity<List<OrderStatusResponse>> searchOrderStatuses(@RequestParam String name) {
        List<OrderStatuses> orderStatuses = orderStatusService.getAllOrderStatuses().stream()
                .filter(status -> status.getName().toLowerCase().contains(name.toLowerCase()))
                .toList();

        List<OrderStatusResponse> responses = orderStatuses.stream()
                .map(orderStatus -> OrderStatusResponse.builder()
                        .id(orderStatus.getId())
                        .name(orderStatus.getName())
                        .color(orderStatus.getColor())
                        .build())
                .collect(Collectors.toList());

        return ResponseEntity.ok(responses);
    }

    @GetMapping("/check-name")
    public ResponseEntity<Boolean> checkNameExists(@RequestParam String name) {
        boolean exists = orderStatusService.existsByName(name);
        return ResponseEntity.ok(exists);
    }
}