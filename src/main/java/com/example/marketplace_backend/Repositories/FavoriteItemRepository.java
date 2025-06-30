package com.example.marketplace_backend.Repositories;

import com.example.marketplace_backend.Model.Intermediate_objects.FavoriteItem;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FavoriteItemRepository {
    Optional<FavoriteItem> findByUserId(String userId);
}
