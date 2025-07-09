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
public interface SubcategoryRepository extends JpaRepository<Subcategory, UUID> {

    Subcategory findByIdAndDeletedAtIsNull(UUID id);

    Subcategory findByIdAndDeletedAtIsNotNull(UUID id);

    @Query("SELECT s FROM Subcategory s WHERE s.deletedAt IS NOT NULL")
    List<Subcategory> findAllDeActive();

    @Query("SELECT s FROM Subcategory s WHERE s.deletedAt IS NULL")
    List<Subcategory> findAllActive();

    @Query("SELECT s FROM Subcategory s WHERE s.category = :category AND s.deletedAt IS NULL")
    List<Subcategory> findByCategoryActive(Category category);

    @Query("SELECT s FROM Subcategory s WHERE s.category = :category AND s.deletedAt IS NOT NULL")
    List<Subcategory> findByCategoryDeActive(Category category);

    @Modifying
    @Transactional
    @Query("UPDATE Subcategory s SET s.deletedAt = :deletedAt WHERE s.id = :id")
    void softDeleteById(@Param("id") UUID id, @Param("deletedAt") LocalDateTime deletedAt);

    Page<Subcategory> findAllByDeletedAtIsNull(Pageable pageable);

    Page<Subcategory> findByCategoryAndDeletedAtIsNull(Category category, Pageable pageable);

    Page<Subcategory> findByNameContainingIgnoreCaseAndDeletedAtIsNull(String name, Pageable pageable);

}
