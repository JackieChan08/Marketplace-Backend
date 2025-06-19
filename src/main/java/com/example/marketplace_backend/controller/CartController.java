package com.example.marketplace_backend.controller;

import com.example.marketplace_backend.Model.Cart;
import com.example.marketplace_backend.Model.CartItem;
import com.example.marketplace_backend.Service.Impl.CartService;
import com.example.marketplace_backend.Service.Impl.ProductServiceImpl;
import com.example.marketplace_backend.Service.Impl.UserServiceImpl;
import com.example.marketplace_backend.controller.Requests.models.CartRequest;
import com.example.marketplace_backend.controller.Responses.CartResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/cart")
@Tag(name = "Cart API", description = "API для управления корзиной пользователей")
public class CartController {

    private final CartService cartService;
    private final ProductServiceImpl productService;
    private final UserServiceImpl userService;

    public CartController(CartService cartService,
                          ProductServiceImpl productService,
                          UserServiceImpl userService) {
        this.cartService = cartService;
        this.productService = productService;
        this.userService = userService;
    }

    private UUID extractUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        return userService.findByEmail(email).getId();
    }

    @PostMapping("/add")
    public ResponseEntity<CartResponse> addItem(@RequestBody CartRequest request) {
        UUID userId = extractUserId();
        Cart cart = cartService.addItemToCart(userId, request.getProductId(), request.getQuantity());
        return ResponseEntity.ok(cartService.convertToCartResponse(cart));
    }

    @DeleteMapping("/remove")
    public ResponseEntity<String> removeItem(@RequestBody CartRequest request) {
        UUID userId = extractUserId();
        cartService.removeItemFromCart(userId, request.getProductId());
        return ResponseEntity.ok("Removed product with ID: " + request.getProductId());
    }

    @DeleteMapping("/clear")
    public ResponseEntity<String> clearCart() {
        UUID userId = extractUserId();
        cartService.clearCart(userId);
        return ResponseEntity.ok("Cart cleared");
    }

    @GetMapping
    public ResponseEntity<CartResponse> getCart() {
        UUID userId = extractUserId();
        Cart cart = cartService.getCart(userId);
        return ResponseEntity.ok(cartService.convertToCartResponse(cart));
    }

    @GetMapping("/items")
    public ResponseEntity<List<CartItem>> getCartItems() {
        UUID userId = extractUserId();
        return cartService.getCartItemsByUserId(userId);
    }
}
