package com.example.marketplace_backend.controller;

import com.example.marketplace_backend.Model.*;
import com.example.marketplace_backend.Service.Impl.*;
import com.example.marketplace_backend.Repositories.*;
import com.example.marketplace_backend.controller.Requests.models.OrderRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Order Controller", description = "Управление заказами")
@Controller
@Data
@RequestMapping("/api/orders")
public class OrderController {
    private final ProductServiceImpl productService;
    private final OrderServiceImpl orderService;
    private final UserServiceImpl userService;
    private final CartService cartService;

    public OrderController(ProductServiceImpl productService, OrderServiceImpl orderService, UserServiceImpl userService, CartService cartService) {
        this.productService = productService;
        this.orderService = orderService;
        this.userService = userService;
        this.cartService = cartService;
    }
    @PutMapping("/{orderId}/address")
    public ResponseEntity<Order> updateAddress(@PathVariable Long orderId,
                                               @RequestParam String address) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User user = userService.findByEmail(email);
        if (orderService.getById(orderId).getUser().getId() == user.getId()) {
            return ResponseEntity.ok(orderService.updateOrderAddress(orderId, address));
        } return  ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }
    @PostMapping("/create")
    public ResponseEntity<Order> createOrder(@RequestBody OrderRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        Long userId = userService.findByEmail(email).getId();
        return ResponseEntity.ok(orderService.createOrderFromCart(userId, request));
    }

}
