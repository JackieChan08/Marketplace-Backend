package com.example.marketplace_backend.controller;


import com.example.marketplace_backend.Model.*;
import com.example.marketplace_backend.Service.Impl.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cart")
@Tag(name = "Cart API", description = "API для управления корзиной пользователей")
public class CartController {

    private final CartService cartService;
    private final ProductServiceImpl productService;
    private final UserServiceImpl userService;

    public CartController(CartService cartService, ProductServiceImpl productService, UserServiceImpl userService) {
        this.cartService = cartService;
        this.productService = productService;
        this.userService = userService;
    }

    @Operation(summary = "Получить элементы корзины", description = "Возвращает список товаров в корзине пользователя")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Список товаров получен",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CartItem.class))),
            @ApiResponse(responseCode = "404", description = "Корзина не найдена")
    })
    @GetMapping("/{userId}/items")
    public ResponseEntity<List<CartItem>> getCartItems(@PathVariable Long userId) {
        return cartService.getCartItemsByUserId(userId);
    }

    @Operation(summary = "Получить корзину", description = "Возвращает корзину пользователя")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Корзина получена",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Cart.class))),
            @ApiResponse(responseCode = "404", description = "Корзина не найдена")
    })
    @GetMapping("/{userId}")
    public ResponseEntity<Cart> getCart(@PathVariable Long userId) {
        Cart cart = cartService.getCart(userId);
        return ResponseEntity.ok(cart);
    }

    @Operation(summary = "Добавить товар в корзину", description = "Добавляет товар в корзину пользователя")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Товар добавлен в корзину",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Cart.class))),
            @ApiResponse(responseCode = "400", description = "Ошибка добавления товара")
    })
    @PostMapping("/add")
    public ResponseEntity<Cart> addItem(@RequestParam("userId") Long userId,
                                        @RequestParam("productId") Long productId,
                                        @RequestParam("quantity") int quantity) {
        double price = productService.getById(productId).getPrice();
        Cart cart = cartService.addItemToCart(userId, productId, quantity, price);
        return ResponseEntity.ok(cart);
    }

    @Operation(summary = "Удалить товар из корзины", description = "Удаляет товар из корзины пользователя")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Товар удален из корзины"),
            @ApiResponse(responseCode = "404", description = "Товар не найден в корзине")
    })
    @DeleteMapping("/remove")
    public ResponseEntity<String> removeItem(@RequestParam("userId") Long userId, @RequestParam("productId") Long productId) {
        cartService.removeItemFromCart(userId, productId);
        return ResponseEntity.ok("Removed " + productId);
    }

    @Operation(summary = "Очистить корзину", description = "Удаляет все товары из корзины пользователя")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Корзина очищена"),
            @ApiResponse(responseCode = "404", description = "Корзина не найдена")
    })
    @DeleteMapping("/clear/{userId}")
    public ResponseEntity<String> clearCart(@PathVariable Long userId) {
        cartService.clearCart(userId);
        return ResponseEntity.ok("Cart cleared");
    }
}
