package com.example.marketplace_backend.controller;

import com.example.marketplace_backend.DTO.Requests.models.FavoriteRequest;
import com.example.marketplace_backend.DTO.Responses.models.BrandResponse;
import com.example.marketplace_backend.DTO.Responses.models.FavoriteItemResponse;
import com.example.marketplace_backend.DTO.Responses.models.FavoriteResponse;
import com.example.marketplace_backend.Model.Brand;
import com.example.marketplace_backend.Model.Favorite;
import com.example.marketplace_backend.Model.Intermediate_objects.FavoriteItem;
import com.example.marketplace_backend.Service.Impl.ConverterService;
import com.example.marketplace_backend.Service.Impl.FavoriteService;
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
@RequestMapping("/api/favorite")
@RequiredArgsConstructor
@Tag(name = "Favorite API", description = "API для управления избранными товарами пользователей")
public class FavoriteController {
    private final FavoriteService favoriteService;
    private final ConverterService converterService;

    @PostMapping(value = "/add", consumes = {"multipart/form-data"})
    public ResponseEntity<FavoriteResponse> addItem(@ModelAttribute FavoriteRequest request) {
        Favorite favorite = favoriteService.addItemToFavorite(request.getProductVariantId(), request.getQuantity());
        return ResponseEntity.ok(converterService.convertToFavoriteResponse(favorite));
    }

    @DeleteMapping("/remove")
    public ResponseEntity<String> removeItem(@ModelAttribute FavoriteRequest request) {
        favoriteService.removeItemFromFavorite(request.getProductVariantId());
        return ResponseEntity.ok("Removed product with ID: " + request.getProductVariantId());
    }

    @DeleteMapping("/clear")
    public ResponseEntity<String> clearFavorite() {
        favoriteService.clearFavorite();
        return ResponseEntity.ok("Favorite cleared");
    }

    @GetMapping
    public ResponseEntity<FavoriteResponse> getFavorite() {
        Favorite favorite = favoriteService.getFavorite();
        return ResponseEntity.ok(converterService.convertToFavoriteResponse(favorite));
    }

//    @GetMapping("/items")
//    public ResponseEntity<List<FavoriteItem>> getFavoriteItems() {
//        return favoriteService.getFavoriteItemsByUserId();
//    }
    @GetMapping("/items")
    public ResponseEntity<Page<FavoriteItemResponse>> getPaginatedFavoriteItems(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<FavoriteItem> favoriteItems = favoriteService.findAllItems(pageable);

        Page<FavoriteItemResponse> responses = favoriteItems.map(converterService::convertToFavoriteItemResponse);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/product-ids")
    public ResponseEntity<List<UUID>> getProductIds() {
        Favorite favorite = favoriteService.getFavorite();

        List<UUID> productIds = favorite.getFavoriteItems().stream()
                .map(item -> item.getProductVariant().getId())
                .toList();

        return ResponseEntity.ok(productIds);
    }
}

