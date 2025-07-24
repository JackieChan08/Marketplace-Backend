package com.example.marketplace_backend.Service.Impl;

import com.example.marketplace_backend.Model.*;
import com.example.marketplace_backend.Model.Intermediate_objects.FavoriteItem;
import com.example.marketplace_backend.Repositories.*;
import com.example.marketplace_backend.DTO.Responses.models.FavoriteItemResponse;
import com.example.marketplace_backend.DTO.Responses.models.FavoriteResponse;
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
public class FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final FavoriteItemRepository favoriteItemRepository;
    private final ProductServiceImpl productService;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final UserServiceImpl userService;

    @Autowired
    public FavoriteService(FavoriteRepository favoriteRepository,
                           FavoriteItemRepository favoriteItemRepository,
                           ProductServiceImpl productService,
                           UserRepository userRepository,
                           ProductRepository productRepository,
                           UserServiceImpl userService) {
        this.favoriteRepository = favoriteRepository;
        this.favoriteItemRepository = favoriteItemRepository;
        this.productService = productService;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.userService = userService;
    }

    public Favorite getFavorite() {
        return favoriteRepository.findFavoriteByUserId(extractUser().getId()).orElseGet(() -> {
            Favorite newFavorite = new Favorite();
            newFavorite.setUser(extractUser());
            newFavorite.setFavoriteItems(new ArrayList<>());
            return favoriteRepository.save(newFavorite);
        });
    }

    private User extractUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        return userService.findByEmail(email);
    }

    @Transactional
    public Favorite addItemToFavorite(UUID productId, int quantity) {
        Favorite favorite = getFavorite();
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        Optional<FavoriteItem> existingItem = favorite.getFavoriteItems().stream()
                .filter(item -> item.getProduct().equals(product))
                .findFirst();

        BigDecimal productPrice = getProductPrice(productId);

        if (existingItem.isPresent()) {
            FavoriteItem item = existingItem.get();
            item.setQuantity(item.getQuantity() + quantity);
        } else {
            FavoriteItem newItem = new FavoriteItem();
            newItem.setFavorite(favorite);
            newItem.setProduct(product);
            newItem.setQuantity(quantity);
            newItem.setPrice(productPrice);
            favorite.getFavoriteItems().add(newItem);
        }

        return favoriteRepository.save(favorite);
    }

    private BigDecimal getProductPrice(UUID productId) {
        return productService.getById(productId).getPrice();
    }

    @Transactional
    public void removeItemFromFavorite(UUID productId) {
        Favorite favorite = getFavorite();
        favorite.getFavoriteItems().removeIf(item -> item.getProduct().getId().equals(productId));
        favoriteRepository.save(favorite);
    }

    @Transactional
    public void clearFavorite() {
        Favorite favorite = getFavorite();
        favorite.getFavoriteItems().clear();
        favoriteRepository.save(favorite);
    }

    public ResponseEntity<List<FavoriteItem>> getFavoriteItemsByUserId() {
        return favoriteRepository.findFavoriteByUserId(extractUser().getId())
                .map(favorite -> ResponseEntity.ok(favorite.getFavoriteItems()))
                .orElse(ResponseEntity.notFound().build());
    }

    public FavoriteResponse convertToFavoriteResponse(Favorite favorite) {
        List<FavoriteItemResponse> items = favorite.getFavoriteItems().stream().map(item -> {
            BigDecimal totalPrice = item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
            return FavoriteItemResponse.builder()
                    .productId(item.getProduct().getId())
                    .productName(productService.getById(item.getProduct().getId()).getName())
                    .quantity(item.getQuantity())
                    .pricePerItem(item.getPrice())
                    .totalPrice(totalPrice)
                    .favoriteItemId(item.getId())
                    .addedAt(item.getAddedAt())
                    .build();
        }).toList();

        BigDecimal total = items.stream()
                .map(FavoriteItemResponse::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return FavoriteResponse.builder()
                .items(items)
                .totalPrice(total)
                .build();
    }
}
