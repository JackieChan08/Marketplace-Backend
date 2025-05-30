package com.example.marketplace_backend.controller;

import com.example.marketplace_backend.Model.*;
import com.example.marketplace_backend.Service.Impl.*;
import com.example.marketplace_backend.Repositories.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Tag(name = "Order Controller", description = "Управление заказами")
@Controller
@Data
@RequestMapping("/orders")
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


    @PostMapping("/create_order")
    public ResponseEntity<Order> createOrder(@RequestBody Order order) {
        if (order.getProduct() == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        orderService.save(order);
        return new ResponseEntity<>(order, HttpStatus.CREATED);

    }


    @PostMapping("/{productId}/create")
    public ResponseEntity<Order> orderById(@PathVariable Long productId
    ) {
        Product product = productService.getById(productId);
        Order order = new Order();
        order.setProduct(product);

//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        String currentPrincipalName = authentication.getName();
//        User user = userService.findByLogin(currentPrincipalName);
//
//        order.setUser(user);
        orderService.save(order);
        if (order.getProduct() == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(order, HttpStatus.CREATED);
    }

    @PostMapping("/cart/{cartId}/create")
    public ResponseEntity<List<Order>> orderCartById(
            @PathVariable Long cartId
    ) {
        Cart cart = cartService.getCart(cartId);

        cart.getItems().forEach(item -> {
            for (int i = 0; i < item.getQuantity(); i++) {
                Order order = new Order();
                order.setProduct(productService.getById(item.getProductId()));
                order.setUser(userService.getById(cart.getUserId()));
                orderService.save(order);
            }
        });
        List<Order> orders = userService.ordersByUser(userService.getById(cart.getUserId()));
        cartService.clearCart(cartId);
        return new ResponseEntity<>(orders, HttpStatus.CREATED);
    }
}
