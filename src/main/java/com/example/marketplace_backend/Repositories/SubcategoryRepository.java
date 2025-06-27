package com.example.marketplace_backend.Repositories;

import com.example.marketplace_backend.Model.Brand;
import com.example.marketplace_backend.Model.Category;
import com.example.marketplace_backend.Model.Product;
import com.example.marketplace_backend.Model.Subcategory;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SubcategoryRepository extends JpaRepository<Subcategory, UUID> {
    @Query("SELECT s FROM Subcategory s WHERE s.id = :id AND s.deletedAt IS NOT NULL")
    Optional<Subcategory> findAllDeActive(@Param("id") UUID id);

    @Query("SELECT s FROM Subcategory s WHERE s.id = :id AND s.deletedAt IS NULL")
    Optional<Subcategory> findAllActive(@Param("id") UUID id);

    @Query("SELECT s FROM Subcategory s WHERE s.category = :category AND s.deletedAt IS NULL")
    List<Subcategory> findByCategoryActive(Category category);

    @Query("SELECT s FROM Subcategory s WHERE s.category = :category AND s.deletedAt IS NOT NULL")
    List<Subcategory> findByCategoryDeActive(Category category);

    @Modifying
    @Transactional
    @Query("DELETE FROM Subcategory s WHERE s.deletedAt IS NOT NULL AND s.deletedAt < :expirationDate")
    void purgeOldSubcategories(LocalDateTime expirationDate);

    @Modifying
    @Transactional
    @Query("UPDATE Subcategory s SET s.deletedAt = :deletedAt WHERE s.id = :id")
    void softDeleteById(@Param("id") UUID id, @Param("deletedAt") LocalDateTime deletedAt);
}
