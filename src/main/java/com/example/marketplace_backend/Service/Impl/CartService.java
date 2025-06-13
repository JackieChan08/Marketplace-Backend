package com.example.marketplace_backend.Service.Impl;

import com.example.marketplace_backend.Model.*;
import com.example.marketplace_backend.Repositories.*;
import com.example.marketplace_backend.controller.Responses.CartItemResponse;
import com.example.marketplace_backend.controller.Responses.CartResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
@Service
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductServiceImpl productService;

    @Autowired
    public CartService(CartRepository cartRepository,
                       CartItemRepository cartItemRepository,
                       ProductServiceImpl productService) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.productService = productService;
    }

    public Cart getCart(Long userId) {
        return cartRepository.findById(userId).orElseGet(() -> {
            Cart newCart = new Cart();
            newCart.setUserId(userId);
            newCart.setItems(new ArrayList<>());
            return cartRepository.save(newCart);
        });
    }

    @Transactional
    public Cart addItemToCart(Long userId, Long productId, int quantity) {
        Cart cart = getCart(userId);

        Optional<CartItem> existingItem = cart.getItems().stream()
                .filter(item -> item.getProductId().equals(productId))
                .findFirst();

        double productPrice = getProductPrice(productId);

        if (existingItem.isPresent()) {
            CartItem item = existingItem.get();
            item.setQuantity(item.getQuantity() + quantity);
        } else {
            CartItem newItem = new CartItem();
            newItem.setCart(cart);
            newItem.setProductId(productId);
            newItem.setQuantity(quantity);
            newItem.setPrice(productPrice);
            cart.getItems().add(newItem);
        }

        return cartRepository.save(cart);
    }

    private double getProductPrice(Long productId) {
        return productService.getById(productId).getPrice();
    }

    @Transactional
    public void removeItemFromCart(Long userId, Long productId) {
        Cart cart = getCart(userId);
        cart.getItems().removeIf(item -> item.getProductId().equals(productId));
        cartRepository.save(cart);
    }

    @Transactional
    public void clearCart(Long userId) {
        Cart cart = getCart(userId);
        cart.getItems().clear();
        cartRepository.save(cart);
    }

    public ResponseEntity<List<CartItem>> getCartItemsByUserId(Long userId) {
        return cartRepository.findById(userId)
                .map(cart -> ResponseEntity.ok(cart.getItems()))
                .orElse(ResponseEntity.notFound().build());
    }

    public CartResponse convertToCartResponse(Cart cart) {
        List<CartItemResponse> items = cart.getItems().stream().map(item -> {
            double totalPrice = item.getQuantity() * item.getPrice();
            return CartItemResponse.builder()
                    .productId(item.getProductId())
                    .productName(productService.getById(item.getProductId()).getName())
                    .quantity(item.getQuantity())
                    .pricePerItem(item.getPrice())
                    .totalPrice(totalPrice)
                    .build();
        }).toList();

        double total = items.stream()
                .mapToDouble(CartItemResponse::getTotalPrice)
                .sum();

        return CartResponse.builder()
                .items(items)
                .totalPrice(total)
                .build();
    }
}
