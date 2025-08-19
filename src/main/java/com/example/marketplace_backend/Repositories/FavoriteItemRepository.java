package com.example.marketplace_backend.Repositories;

import com.example.marketplace_backend.Model.Intermediate_objects.FavoriteItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface FavoriteItemRepository extends JpaRepository<FavoriteItem, UUID> {
    List<FavoriteItem> findByFavoriteId(UUID favoriteId);
    void deleteByFavoriteIdAndProductId(UUID favoriteId, UUID productId);
}