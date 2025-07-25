package com.example.marketplace_backend.controller.admin;

import com.example.marketplace_backend.DTO.Responses.models.OrderResponse;
import com.example.marketplace_backend.Model.Order;
import com.example.marketplace_backend.Model.Statuses;
import com.example.marketplace_backend.Service.Impl.ConverterService;
import com.example.marketplace_backend.Service.Impl.OrderServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/orders")
public class AdminOrderController {
    private final OrderServiceImpl orderService;
    private final ConverterService converterService;
    @GetMapping
    public ResponseEntity<Page<OrderResponse>> getAllOrdersPaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Order> orders = orderService.getAllOrders(pageable);
        Page<OrderResponse> responsePage = orders.map(converterService::convertToOrderResponse);
        return ResponseEntity.ok(responsePage);
    }

    @GetMapping("/wholesale")
    public ResponseEntity<Page<OrderResponse>> getWholesaleOrdersPaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Order> orders = orderService.getAllWholesaleOrders(pageable);
        Page<OrderResponse> responsePage = orders.map(converterService::convertToOrderResponse);
        return ResponseEntity.ok(responsePage);
    }

    @GetMapping("/retail")
    public ResponseEntity<Page<OrderResponse>> getRetailOrdersPaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Order> orders = orderService.getAllRetailOrders(pageable);
        Page<OrderResponse> responsePage = orders.map(converterService::convertToOrderResponse);
        return ResponseEntity.ok(responsePage);
    }

    @PutMapping("/{orderId}/comment")
    public ResponseEntity<OrderResponse> updateComment(@PathVariable UUID orderId,
                                                       @RequestParam String comment) {
        Order order = orderService.updateOrderComment(orderId, comment);
        return ResponseEntity.ok(converterService.convertToOrderResponse(order));
    }

    @PutMapping("/{orderId}/address")
    public ResponseEntity<OrderResponse> updateAddress(@PathVariable UUID orderId,
                                                       @RequestParam String address) {
        Order order = orderService.updateOrderAddress(orderId, address);
        return ResponseEntity.ok(converterService.convertToOrderResponse(order));
    }

    @GetMapping("/wholesale/all")
    public ResponseEntity<List<OrderResponse>> getAllWholesaleOrders() {
        List<Order> orders = orderService.getAllWholesaleOrders();
        List<OrderResponse> responses = orders.stream()
                .map(converterService::convertToOrderResponse)
                .toList();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/retail/all")
    public ResponseEntity<List<OrderResponse>> getAllRetailOrders() {
        List<Order> orders = orderService.getAllRetailOrders();
        List<OrderResponse> responses = orders.stream()
                .map(converterService::convertToOrderResponse)
                .toList();
        return ResponseEntity.ok(responses);
    }

}