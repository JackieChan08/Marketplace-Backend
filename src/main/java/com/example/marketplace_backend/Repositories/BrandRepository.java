package com.example.marketplace_backend.Repositories;

import com.example.marketplace_backend.Model.Brand;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface BrandRepository extends JpaRepository<Brand, UUID> {
    @Query("SELECT b FROM Brand b WHERE b.deletedAt IS NULL")
    List<Brand> findAllActive();

    List<Brand> findAllByDeletedAtIsNull();

    @Query("SELECT b FROM Brand b WHERE b.id = :id AND b.deletedAt IS NULL")
    Optional<Brand> findActiveById(UUID id);

    // бренды с продуктами
    @Query("SELECT b FROM Brand b LEFT JOIN FETCH b.products WHERE b.deletedAt IS NULL")
    List<Brand> findAllWithProducts();

    @Modifying
    @Transactional
    @Query("DELETE FROM Brand b WHERE b.deletedAt IS NOT NULL AND b.deletedAt < :expirationDate")
    void purgeOldBrands(LocalDateTime expirationDate);
}

