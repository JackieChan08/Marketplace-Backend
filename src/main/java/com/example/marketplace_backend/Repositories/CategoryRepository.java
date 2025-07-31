package com.example.marketplace_backend.Repositories;


import com.example.marketplace_backend.Model.Category;
import com.example.marketplace_backend.Model.Subcategory;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface CategoryRepository extends JpaRepository<Category, UUID> {
    Category findByName(String name);

    @Query("SELECT c FROM Category c WHERE c.deletedAt IS NULL ORDER BY c.createdAt DESC")
    List<Category> findAllActive();

    @Query("SELECT c FROM Category c WHERE c.deletedAt IS NULL ORDER BY c.createdAt DESC")
    Page<Category> findAllActive(Pageable  pageable);

    @Query("SELECT c FROM Category c WHERE c.deletedAt IS NOT NULL ORDER BY c.createdAt DESC")
    List<Category> findAllDeActive();

    @Query("SELECT c FROM Category c WHERE c.deletedAt IS NOT NULL ORDER BY c.createdAt DESC")
    Page<Category> findAllDeActive(Pageable  pageable);

    @Query("SELECT DISTINCT c FROM Category c LEFT JOIN FETCH c.subcategories s WHERE c.deletedAt IS NULL AND (s.deletedAt IS NULL OR s IS NULL) ORDER BY c.createdAt DESC")
    List<Category> findAllActiveWithSubcategories();

    @Modifying
    @Transactional
    @Query("UPDATE Category c SET c.deletedAt = :deletedAt WHERE c.id = :id")
    void softDeleteById(@Param("id") UUID id, @Param("deletedAt") LocalDateTime deletedAt);

    @Query("select c from Category c where c.priority = true")
    Page<Category> findCategoriesByPriority(Pageable pageable);
}
