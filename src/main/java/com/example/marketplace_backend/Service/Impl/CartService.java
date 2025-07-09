package com.example.marketplace_backend.Service.Impl;

import com.example.marketplace_backend.Model.*;
import com.example.marketplace_backend.Model.Intermediate_objects.CartItem;
import com.example.marketplace_backend.Repositories.*;
import com.example.marketplace_backend.DTO.Responses.models.CartItemResponse;
import com.example.marketplace_backend.DTO.Responses.models.CartResponse;
import com.example.marketplace_backend.Service.Impl.auth.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductServiceImpl productService;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final UserServiceImpl  userService;

    @Autowired
    public CartService(CartRepository cartRepository,
                       CartItemRepository cartItemRepository,
                       ProductServiceImpl productService,
                       UserRepository userRepository, ProductRepository productRepository, UserServiceImpl userService) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.productService = productService;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.userService = userService;
    }

    public Cart getCart() {
        return cartRepository.findByUser(extractUser()).orElseGet(() -> {
            Cart newCart = new Cart();
            newCart.setUser(extractUser());
            newCart.setCartItems(new ArrayList<>());
            return cartRepository.save(newCart);
        });
    }

    private User extractUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        return userService.findByEmail(email);
    }

    @Transactional
    public Cart addItemToCart(UUID productId, int quantity) {

        Cart cart = getCart();
        Product product = productRepository.findById(productId).orElseThrow(() -> new RuntimeException("Product not found"));

        Optional<CartItem> existingItem = cart.getCartItems().stream()
                .filter(item -> item.getProduct().equals(product))
                .findFirst();

        BigDecimal productPrice = getProductPrice(productId);

        if (existingItem.isPresent()) {
            CartItem item = existingItem.get();
            item.setQuantity(item.getQuantity() + quantity);
        } else {

            CartItem newItem = new CartItem();
            newItem.setCart(cart);
            newItem.setProduct(product);
            newItem.setQuantity(quantity);
            newItem.setPrice(productPrice);
            cart.getCartItems().add(newItem);
        }

        return cartRepository.save(cart);
    }

    private BigDecimal getProductPrice(UUID productId) {
        return productService.getById(productId).getPrice();
    }

    @Transactional
    public void removeItemFromCart(UUID productId) {
        Cart cart = getCart();
        cart.getCartItems().removeIf(item -> item.getProduct().getId().equals(productId));
        cartRepository.save(cart);
    }

    @Transactional
    public void clearCart() {
        Cart cart = getCart();
        cart.getCartItems().clear();
        cartRepository.save(cart);
    }

    public ResponseEntity<List<CartItem>> getCartItemsByUserId() {
        return cartRepository.findByUser(extractUser())
                .map(cart -> ResponseEntity.ok(cart.getCartItems()))
                .orElse(ResponseEntity.notFound().build());
    }

    public CartResponse convertToCartResponse(Cart cart) {
        List<CartItemResponse> items = cart.getCartItems().stream().map(item -> {
            BigDecimal totalPrice = item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
            return CartItemResponse.builder()
                    .productId(item.getProduct().getId())
                    .productName(productService.getById(item.getProduct().getId()).getName())
                    .quantity(item.getQuantity())
                    .pricePerItem(item.getPrice())
                    .totalPrice(totalPrice)
                    .build();
        }).toList();

        BigDecimal total = items.stream()
                .map(CartItemResponse::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return CartResponse.builder()
                .items(items)
                .totalPrice(total)
                .build();
    }
}

