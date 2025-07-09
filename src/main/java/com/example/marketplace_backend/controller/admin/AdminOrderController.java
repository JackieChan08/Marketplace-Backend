package com.example.marketplace_backend.controller.admin;

import com.example.marketplace_backend.Model.Order;
import com.example.marketplace_backend.Model.Statuses;
import com.example.marketplace_backend.Service.Impl.OrderServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/orders")
public class AdminOrderController {
    private final OrderServiceImpl orderService;


    @GetMapping
    public ResponseEntity<Page<Order>> getAllOrdersPaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(orderService.getAllOrders(pageable));
    }

    @GetMapping("/wholesale")
    public ResponseEntity<Page<Order>> getWholesaleOrdersPaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(orderService.getAllWholesaleOrders(pageable));
    }

    @GetMapping("/retail")
    public ResponseEntity<Page<Order>> getRetailOrdersPaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(orderService.getAllRetailOrders(pageable));
    }

    @GetMapping("/by-status/{statusId}")
    public ResponseEntity<Page<Order>> getOrdersByStatusPaginated(
            @PathVariable UUID statusId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(orderService.getOrdersByStatus(statusId, pageable));
    }

    @GetMapping("/by-status-name")
    public ResponseEntity<Page<Order>> getOrdersByStatusNamePaginated(
            @RequestParam String statusName,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(orderService.getOrdersByStatusName(statusName, pageable));
    }

    @PostMapping("/{orderId}/status")
    public ResponseEntity<Order> addStatusToOrder(@PathVariable UUID orderId,
                                                  @RequestParam UUID statusId) {
        return ResponseEntity.ok(orderService.addStatusToOrder(orderId, statusId));
    }

    @PostMapping("/{orderId}/status-by-name")
    public ResponseEntity<Order> addStatusToOrderByName(@PathVariable UUID orderId,
                                                        @RequestParam String statusName) {
        return ResponseEntity.ok(orderService.addStatusToOrderByName(orderId, statusName));
    }

    @DeleteMapping("/{orderId}/status/{statusId}")
    public ResponseEntity<Order> removeStatusFromOrder(@PathVariable UUID orderId,
                                                       @PathVariable UUID statusId) {
        return ResponseEntity.ok(orderService.removeStatusFromOrder(orderId, statusId));
    }

    @GetMapping("/{orderId}/statuses")
    public ResponseEntity<List<Statuses>> getOrderStatuses(@PathVariable UUID orderId) {
        return ResponseEntity.ok(orderService.getOrderStatuses(orderId));
    }

    @PutMapping("/{orderId}/comment")
    public ResponseEntity<Order> updateComment(@PathVariable UUID orderId,
                                               @RequestParam String comment) {
        return ResponseEntity.ok(orderService.updateOrderComment(orderId, comment));
    }

    @PutMapping("/{orderId}/address")
    public ResponseEntity<Order> updateAddress(@PathVariable UUID orderId,
                                               @RequestParam String address) {
        return ResponseEntity.ok(orderService.updateOrderAddress(orderId, address));
    }

    @GetMapping("/by-status/{statusId}")
    public ResponseEntity<List<Order>> getOrdersWithStatus(@PathVariable UUID statusId) {
        return ResponseEntity.ok(orderService.getOrdersWithStatus(statusId));
    }

    @GetMapping("/by-status-name")
    public ResponseEntity<List<Order>> getOrdersByStatusName(@RequestParam String statusName) {
        return ResponseEntity.ok(orderService.getOrdersByStatusName(statusName));
    }

}