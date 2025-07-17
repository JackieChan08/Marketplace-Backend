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

    @Query("SELECT c FROM Category c WHERE c.deletedAt IS NULL")
    List<Category> findAllActive();

    @Query("SELECT c FROM Category c WHERE c.deletedAt IS NOT NULL")
    List<Category> findAllDeActive();

    @Query("SELECT c FROM Category c WHERE c.deletedAt IS NOT NULL")
    Page<Category> findAllDeActive(Pageable  pageable);

    @Modifying
    @Transactional
    @Query("UPDATE Category c SET c.deletedAt = :deletedAt WHERE c.id = :id")
    void softDeleteById(@Param("id") UUID id, @Param("deletedAt") LocalDateTime deletedAt);

}
