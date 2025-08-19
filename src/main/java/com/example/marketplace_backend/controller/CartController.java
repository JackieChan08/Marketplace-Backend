package com.example.marketplace_backend.controller;

import com.example.marketplace_backend.DTO.Requests.models.CartRequest;
import com.example.marketplace_backend.DTO.Responses.models.CartItemResponse;
import com.example.marketplace_backend.DTO.Responses.models.CartResponse;
import com.example.marketplace_backend.DTO.Responses.models.FavoriteItemResponse;
import com.example.marketplace_backend.Model.Cart;
import com.example.marketplace_backend.Model.Intermediate_objects.CartItem;
import com.example.marketplace_backend.Model.Intermediate_objects.FavoriteItem;
import com.example.marketplace_backend.Service.Impl.CartService;
import com.example.marketplace_backend.Service.Impl.ConverterService;
import com.example.marketplace_backend.Service.Impl.ProductServiceImpl;
import com.example.marketplace_backend.Service.Impl.auth.UserServiceImpl;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
@Tag(name = "Cart API", description = "API для управления корзиной пользователей")
public class CartController {

    private final CartService cartService;
    private final ConverterService converterService;

    @PostMapping(value = "/add", consumes = {"multipart/form-data"})
    public ResponseEntity<CartResponse> addItem(@ModelAttribute CartRequest request) {
        Cart cart = cartService.addItemToCart(request.getProductId(), request.getQuantity());
        return ResponseEntity.ok(converterService.convertToCartResponse(cart));
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
        return ResponseEntity.ok(converterService.convertToCartResponse(cart));
    }

    @GetMapping("/items")
    public ResponseEntity<Page<CartItemResponse>> getPaginatedCartItems(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
       Pageable pageable = PageRequest.of(page, size);
       Page<CartItem> cartItems = cartService.findAllItems(pageable);

       Page<CartItemResponse> responses = cartItems.map(converterService::convertToCartItemResponse);
       return ResponseEntity.ok(responses);
    }

    @GetMapping("/product-ids")
    public ResponseEntity<List<UUID>> getProductIds() {
        Cart cart = cartService.getCart();

        List<UUID> productIds = cart.getCartItems().stream()
                .map(item -> item.getProduct().getId())
                .toList();

        return ResponseEntity.ok(productIds);
    }

    @PostMapping("/update-quantity")
    public ResponseEntity<CartResponse> updateItemQuantity(
            @RequestParam UUID productId,
            @RequestParam int quantity
    ) {
        Cart updatedCart = cartService.updateItemQuantity(productId, quantity);
        return ResponseEntity.ok(converterService.convertToCartResponse(updatedCart));
    }


//    @GetMapping("/items")
//    public ResponseEntity<Page<FavoriteItemResponse>> getPaginatedFavoriteItems(
//            @RequestParam(defaultValue = "0") int page,
//            @RequestParam(defaultValue = "10") int size
//    ) {
//        Pageable pageable = PageRequest.of(page, size);
//        Page<FavoriteItem> favoriteItems = favoriteService.findAllItems(pageable);
//
//        Page<FavoriteItemResponse> responses = favoriteItems.map(converterService::convertToFavoriteItemResponse);
//        return ResponseEntity.ok(responses);
//    }
}
