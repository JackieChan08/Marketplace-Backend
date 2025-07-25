package com.example.marketplace_backend.Repositories;

import com.example.marketplace_backend.Model.Brand;
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
import java.util.Optional;
import java.util.UUID;

@Repository
public interface BrandRepository extends JpaRepository<Brand, UUID> {
    @Query("SELECT b FROM Brand b WHERE b.deletedAt IS NULL")
    List<Brand> findAllActive();

    @Query("SELECT b FROM Brand b WHERE b.deletedAt IS NOT NULL")
    List<Brand> findAllDeActive();

    @Query("SELECT b FROM Brand b WHERE b.deletedAt IS NOT NULL")
    Page<Brand> findAllDeActive(Pageable pageable);

    @Query("SELECT b FROM Brand b WHERE b.deletedAt IS NULL")
    Page<Brand> findAllActive(Pageable pageable);

    // бренды с продуктами
    @Query("SELECT b FROM Brand b LEFT JOIN FETCH b.products WHERE b.deletedAt IS NULL")
    List<Brand> findAllWithProducts();

    @Modifying
    @Transactional
    @Query("UPDATE Brand b SET b.deletedAt = :deletedAt WHERE b.id = :id")
    void softDeleteById(@Param("id") UUID id, @Param("deletedAt") LocalDateTime deletedAt);
}

