package com.example.marketplace_backend.controller;

import com.example.marketplace_backend.Model.Order;
import com.example.marketplace_backend.Model.User;
import com.example.marketplace_backend.Service.Impl.OrderServiceImpl;
import com.example.marketplace_backend.Service.Impl.UserServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "User Controller", description = "Управление пользователями")
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserServiceImpl userService;
    private final OrderServiceImpl orderService;

    @GetMapping("/{id}")
    public ResponseEntity<User> getById(@PathVariable Long id
    ) {
        User user = userService.getById(id);
        if (user == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

//    @GetMapping("/my_orders")
//    public ResponseEntity<List<Order>> myOrders() {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        String currentPrincipalName = authentication.getName();
//        User user = userService.findByLogin(currentPrincipalName);
//
//        List<Order> orders = userService.ordersByUser(user);
//        if (orders.isEmpty()) {
//            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
//        }
//        return new ResponseEntity<>(orders, HttpStatus.OK);
//    }

    @PostMapping("/de_active_order/{orderId}")
    public ResponseEntity<Order> deActiveOrder(@PathVariable Long orderId) {
        Order order = orderService.getById(orderId);
        if (order == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        order.setStatus("de_active");
        orderService.save(order);
        return new ResponseEntity<>(order, HttpStatus.OK);
    }
}
