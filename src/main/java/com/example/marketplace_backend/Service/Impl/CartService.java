package com.example.marketplace_backend.Service.Impl;

import com.example.marketplace_backend.Model.*;
import com.example.marketplace_backend.Model.Intermediate_objects.CartItem;
import com.example.marketplace_backend.Repositories.*;
import com.example.marketplace_backend.DTO.Responses.models.CartItemResponse;
import com.example.marketplace_backend.DTO.Responses.models.CartResponse;
import com.example.marketplace_backend.Service.Impl.auth.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
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
    private final ProductVariantRepository productVariantRepository;

    @Autowired
    public CartService(CartRepository cartRepository,
                       CartItemRepository cartItemRepository,
                       ProductServiceImpl productService,
                       UserRepository userRepository,
                       ProductRepository productRepository,
                       UserServiceImpl userService,
                       ProductVariantRepository productVariantRepository) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.productService = productService;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.userService = userService;
        this.productVariantRepository = productVariantRepository;
    }

    public Cart getCart() {
        UUID userId = extractUser().getId();

        return cartRepository.findCartByUserId(userId).orElseGet(() -> {
            Cart newCart = new Cart();
            newCart.setUser(extractUser());
            newCart.setCartItems(new ArrayList<>());
            try {
                return cartRepository.save(newCart);
            } catch (DataIntegrityViolationException e) {
                return cartRepository.findCartByUserId(userId)
                        .orElseThrow(() -> new RuntimeException("Failed to create or retrieve cart"));
            }
        });
    }


    private User extractUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        return userService.findByEmail(email);
    }

    @Transactional
    public Cart addItemToCart(UUID productVariantId, int quantity) {

        Cart cart = getCart();
        ProductVariant productVariant = productVariantRepository.findById(productVariantId).orElseThrow(() -> new RuntimeException("ProductVariant not found"));

        Optional<CartItem> existingItem = cart.getCartItems().stream()
                .filter(item -> item.getProductVariant().equals(productVariant))
                .findFirst();

        if (existingItem.isPresent()) {
            CartItem item = existingItem.get();
            item.setQuantity(item.getQuantity() + quantity);
        } else {

            CartItem newItem = new CartItem();
            newItem.setCart(cart);
            newItem.setProductVariant(productVariant);
            newItem.setQuantity(quantity);
            newItem.setPrice(productVariant.getPrice());
            cart.getCartItems().add(newItem);
        }

        return cartRepository.save(cart);
    }

    @Transactional
    public void removeItemFromCart(UUID productVariantId) {
        Cart cart = getCart();
        cart.getCartItems().removeIf(item -> item.getProductVariant().getId().equals(productVariantId));
        cartRepository.save(cart);
    }

    @Transactional
    public void clearCart() {
        Cart cart = getCart();
        cart.getCartItems().clear();
        cartRepository.save(cart);
    }

    public ResponseEntity<List<CartItem>> getCartItemsByUserId() {
        return cartRepository.findCartByUserId(extractUser().getId())
                .map(cart -> ResponseEntity.ok(cart.getCartItems()))
                .orElse(ResponseEntity.notFound().build());
    }

    public Page<CartItem> findAllItems(Pageable pageable) {
        Cart cart = getCart();
        List<CartItem> cartItems = cart.getCartItems();

        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), cartItems.size());

        List<CartItem> pageContent = cartItems.subList(start, end);

        return new PageImpl<>(pageContent, pageable, cartItems.size());
    }

    public Cart updateItemQuantity(UUID productId, int quantity) {
        Cart cart = getCart();

        CartItem cartItem = cart.getCartItems().stream()
                .filter(item -> item.getProductVariant().getId().equals(productId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Product not found in cart"));

        if (quantity <= 0) {
            cart.getCartItems().remove(cartItem);
            cartItemRepository.delete(cartItem);
        } else {
            cartItem.setQuantity(quantity);
            cartItemRepository.save(cartItem);
        }

        return cartRepository.save(cart);
    }

    public List<UUID> getProductVariantsIds() {
        return getCart().getCartItems().stream()
                .map(ids -> ids.getProductVariant().getId())
                .toList();
    }

    public List<UUID> getProductIds() {
        return getCart().getCartItems().stream()
                .map(ids -> ids.getProductVariant().getProduct().getId())
                .toList();
    }
}