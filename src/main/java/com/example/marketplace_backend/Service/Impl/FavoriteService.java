package com.example.marketplace_backend.Service.Impl;

import com.example.marketplace_backend.Model.*;
import com.example.marketplace_backend.Model.Intermediate_objects.FavoriteItem;
import com.example.marketplace_backend.Repositories.*;
import com.example.marketplace_backend.DTO.Responses.models.FavoriteItemResponse;
import com.example.marketplace_backend.DTO.Responses.models.FavoriteResponse;
import com.example.marketplace_backend.Service.Impl.auth.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
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
public class FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final FavoriteItemRepository favoriteItemRepository;
    private final ProductServiceImpl productService;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final UserServiceImpl userService;
    private final ProductVariantRepository productVariantRepository;

    @Autowired
    public FavoriteService(FavoriteRepository favoriteRepository,
                           FavoriteItemRepository favoriteItemRepository,
                           ProductServiceImpl productService,
                           UserRepository userRepository,
                           ProductRepository productRepository,
                           UserServiceImpl userService,
                           ProductVariantRepository productVariantRepository) {
        this.favoriteRepository = favoriteRepository;
        this.favoriteItemRepository = favoriteItemRepository;
        this.productService = productService;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.userService = userService;
        this.productVariantRepository = productVariantRepository;
    }

    public Favorite getFavorite() {
        return favoriteRepository.findFavoriteByUserId(extractUser().getId()).orElseGet(() -> {
            Favorite newFavorite = new Favorite();
            newFavorite.setUser(extractUser());
            newFavorite.setFavoriteItems(new ArrayList<>());
            return favoriteRepository.save(newFavorite);
        });
    }

    public Page<FavoriteItem> findAllItems(Pageable pageable) {
        return favoriteItemRepository.findAll(pageable);
    }

    private User extractUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        return userService.findByEmail(email);
    }

    @Transactional
    public Favorite addItemToFavorite(UUID productVariantId, int quantity) {
        Favorite favorite = getFavorite();
        ProductVariant productVariant = productVariantRepository.findById(productVariantId).orElseThrow(() -> new RuntimeException("ProductVariant not found"));

        Optional<FavoriteItem> existingItem = favorite.getFavoriteItems().stream()
                .filter(item -> item.getProductVariant().equals(productVariant))
                .findFirst();

        if (existingItem.isPresent()) {
            FavoriteItem item = existingItem.get();
            item.setQuantity(item.getQuantity() + quantity);
        } else {
            FavoriteItem newItem = new FavoriteItem();
            newItem.setFavorite(favorite);
            newItem.setProductVariant(productVariant);
            newItem.setQuantity(quantity);
            BigDecimal price = resolveProductPrice(productVariant);
            newItem.setPrice(price);
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
        favorite.getFavoriteItems().removeIf(item -> item.getProductVariant().getId().equals(productId));
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

    private BigDecimal resolveProductPrice(ProductVariant variant) {
        if (variant.getPhoneSpec() != null && variant.getPhoneSpec().getPrice() != null) {
            return variant.getPhoneSpec().getPrice();
        } else if (variant.getLaptopSpec() != null && variant.getLaptopSpec().getPrice() != null) {
            return variant.getLaptopSpec().getPrice();
        } else if (variant.getProduct() != null && variant.getProduct().getPrice() != null) {
            return variant.getProduct().getPrice();
        } else {
            throw new RuntimeException("Price not defined for product variant: " + variant.getId());
        }
    }

}
