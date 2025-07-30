package com.example.marketplace_backend.controller;

import com.example.marketplace_backend.DTO.Requests.models.FavoriteRequest;
import com.example.marketplace_backend.DTO.Responses.models.FavoriteResponse;
import com.example.marketplace_backend.Model.Favorite;
import com.example.marketplace_backend.Model.Intermediate_objects.FavoriteItem;
import com.example.marketplace_backend.Service.Impl.FavoriteService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/favorite")
@RequiredArgsConstructor
@Tag(name = "Favorite API", description = "API для управления избранными товарами пользователей")
public class FavoriteController {
    private final FavoriteService favoriteService;


    @PostMapping(value = "/add", consumes = {"multipart/form-data"})
    public ResponseEntity<FavoriteResponse> addItem(@ModelAttribute FavoriteRequest request) {
        Favorite favorite = favoriteService.addItemToFavorite(request.getProductId(), request.getQuantity());
        return ResponseEntity.ok(favoriteService.convertToFavoriteResponse(favorite));
    }

    @DeleteMapping("/remove")
    public ResponseEntity<String> removeItem(@ModelAttribute FavoriteRequest request) {
        favoriteService.removeItemFromFavorite(request.getProductId());
        return ResponseEntity.ok("Removed product with ID: " + request.getProductId());
    }

    @DeleteMapping("/clear")
    public ResponseEntity<String> clearFavorite() {
        favoriteService.clearFavorite();
        return ResponseEntity.ok("Favorite cleared");
    }

    @GetMapping
    public ResponseEntity<FavoriteResponse> getFavorite() {
        Favorite favorite = favoriteService.getFavorite();
        return ResponseEntity.ok(favoriteService.convertToFavoriteResponse(favorite));
    }

    @GetMapping("/items")
    public ResponseEntity<List<FavoriteItem>> getFavoriteItems() {
        return favoriteService.getFavoriteItemsByUserId();
    }
}
