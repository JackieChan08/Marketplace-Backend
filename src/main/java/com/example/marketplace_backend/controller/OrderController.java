package com.example.marketplace_backend.controller;

import com.example.marketplace_backend.DTO.Requests.models.OrderRequest;
import com.example.marketplace_backend.DTO.Responses.models.OrderResponse;
import com.example.marketplace_backend.DTO.Responses.models.OrderWholesaleResponse;
import com.example.marketplace_backend.enums.PaymentMethod;
import com.example.marketplace_backend.Model.Order;
import com.example.marketplace_backend.Model.User;
import com.example.marketplace_backend.Service.Impl.CartService;
import com.example.marketplace_backend.Service.Impl.ConverterService;
import com.example.marketplace_backend.Service.Impl.OrderServiceImpl;
import com.example.marketplace_backend.Service.Impl.ProductServiceImpl;
import com.example.marketplace_backend.Service.Impl.auth.UserServiceImpl;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Tag(name = "Order Controller", description = "Управление заказами")
@RestController
@Data
@RequestMapping("/api/orders")
public class OrderController {
    private final ProductServiceImpl productService;
    private final OrderServiceImpl orderService;
    private final UserServiceImpl userService;
    private final CartService cartService;
    private final ConverterService converterService;

    public OrderController(ProductServiceImpl productService, OrderServiceImpl orderService, UserServiceImpl userService, CartService cartService, ConverterService converterService) {
        this.productService = productService;
        this.orderService = orderService;
        this.userService = userService;
        this.cartService = cartService;
        this.converterService = converterService;
    }
    @PutMapping("/{orderId}/address")
    public ResponseEntity<OrderResponse> updateAddress(@PathVariable UUID orderId,
                                                       @RequestParam String address) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User user = userService.findByEmail(email);
        Order updatedOrder = orderService.getById(orderId);

        if (updatedOrder.getUser().getId().equals(user.getId())) {
            Order updated = orderService.updateOrderAddress(orderId, address);
            return ResponseEntity.ok(converterService.convertToOrderResponse(updated));
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    @PostMapping(value = "/create", consumes = {"multipart/form-data"})
    public ResponseEntity<OrderResponse> createOrder(@ModelAttribute OrderRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        UUID userId = userService.findByEmail(email).getId();
        return ResponseEntity.ok(orderService.createOrderFromCart(userId, request));
    }

    @PostMapping(value = "/create-for-all", consumes = {"multipart/form-data"})
    public ResponseEntity<OrderResponse> createFullCart(@ModelAttribute OrderRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        UUID userId = userService.findByEmail(email).getId();
        return ResponseEntity.ok(orderService.createOrderFromCart(userId, request));
    }

    @PostMapping(value = "/create/wholesale", consumes = {"multipart/form-data"})
    public ResponseEntity<OrderWholesaleResponse> createOrderWholesale(@ModelAttribute OrderRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        UUID userId = userService.findByEmail(email).getId();
        return ResponseEntity.ok(orderService.createOrderWholesale(userId, request));
    }

    @PutMapping("/{orderId}/comment")
    public ResponseEntity<OrderResponse> updateComment(@PathVariable UUID orderId,
                                                       @RequestParam String comment) {
        Order updated = orderService.updateOrderComment(orderId, comment);
        return ResponseEntity.ok(converterService.convertToOrderResponse(updated));
    }

    @GetMapping("my_orders")
    public ResponseEntity<Page<OrderResponse>> getMyOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        UUID userId = userService.findByEmail(email).getId();
        Page<Order> orders = orderService.findOrdersByUserId(pageable, userId);

        return ResponseEntity.ok(orders.map(converterService::convertToOrderResponse));
    }

    @GetMapping("/payment-methods")
    public List<Map<String, String>> getPaymentMethods() {
        return Arrays.stream(PaymentMethod.values())
                .map(pm -> Map.of("key", pm.name(), "label", switch (pm) {
                    case CASH -> "Наличными";
                    case TRANSFER -> "Переводом";
                }))
                .toList();
    }
}
