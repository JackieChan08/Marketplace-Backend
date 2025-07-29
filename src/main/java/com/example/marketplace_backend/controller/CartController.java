package com.example.marketplace_backend.controller;

import com.example.marketplace_backend.DTO.Requests.models.CartRequest;
import com.example.marketplace_backend.DTO.Responses.models.CartResponse;
import com.example.marketplace_backend.Model.Cart;
import com.example.marketplace_backend.Model.Intermediate_objects.CartItem;
import com.example.marketplace_backend.Service.Impl.CartService;
import com.example.marketplace_backend.Service.Impl.ProductServiceImpl;
import com.example.marketplace_backend.Service.Impl.auth.UserServiceImpl;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
@Tag(name = "Cart API", description = "API для управления корзиной пользователей")
public class CartController {

    private final CartService cartService;

    @PostMapping(value = "/add", consumes = {"multipart/form-data"})
    public ResponseEntity<CartResponse> addItem(@ModelAttribute CartRequest request) {
        Cart cart = cartService.addItemToCart(request.getProductId(), request.getQuantity());
        return ResponseEntity.ok(cartService.convertToCartResponse(cart));
    }

    @DeleteMapping("/remove")
    public ResponseEntity<String> removeItem(@ModelAttribute CartRequest request) {
        cartService.removeItemFromCart(request.getProductId());
        return ResponseEntity.ok("Removed product with ID: " + request.getProductId());
    }

    @DeleteMapping("/clear")
    public ResponseEntity<String> clearCart() {
        cartService.clearCart();
        return ResponseEntity.ok("Cart cleared");
    }

    @GetMapping
    public ResponseEntity<CartResponse> getCart() {
        Cart cart = cartService.getCart();
        return ResponseEntity.ok(cartService.convertToCartResponse(cart));
    }

    @GetMapping("/items")
    public ResponseEntity<List<CartItem>> getCartItems() {
        return cartService.getCartItemsByUserId();
    }
}
