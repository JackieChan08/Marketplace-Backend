package com.example.marketplace_backend.Repositories;

import com.example.marketplace_backend.Model.Brand;
import com.example.marketplace_backend.Model.Product;
import com.example.marketplace_backend.Model.Subcategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProductRepository extends JpaRepository<Product, UUID>, JpaSpecificationExecutor<Product> {
    Product findByName(String name);

    @Query("SELECT p FROM Product p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :query, '%'))")
    Page<Product> findByNameContaining(@Param("query") String name, Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.deletedAt IS NULL")
    List<Product> findAllActive();

    @Query("SELECT p FROM Product p WHERE p.deletedAt IS NULL")
    Page<Product> findAllActive(Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.deletedAt IS NOT NULL")
    List<Product> findAllDeActive();

    @Query("SELECT p FROM Product p WHERE p.deletedAt IS NOT NULL")
    Page<Product> findAllDeActive(Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.subcategory = :subcategory and p.deletedAt IS NULL")
    List<Product> findActiveBySubcategory(@Param("subcategory") Subcategory subcategory);

    @Query("SELECT p FROM Product p WHERE p.subcategory = :subcategory and p.deletedAt IS NOT NULL")
    List<Product> findDeActiveBySubcategory(@Param("subcategory") Subcategory subcategory);

    @Query("SELECT p FROM Product p WHERE p.brand = :brand and p.deletedAt IS NULL")
    List<Product> findActiveByBrand(@Param("brand") Brand brand);

    @Query("SELECT p FROM Product p WHERE p.brand.id = :brandId and p.deletedAt IS NULL")
    Page<Product> findActiveByBrand(@Param("brandId") UUID brandId, Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.subcategory.id = :subcategoryId and p.deletedAt IS NULL")
    Page<Product> findActiveBySubcategory(@Param("subcategoryId") UUID subcategoryId, Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.subcategory.category.id = :categoryId and p.deletedAt IS NULL")
    Page<Product> findActiveByCategory(@Param("subcategoryId") UUID categoryId, Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.brand = :brand and p.deletedAt IS NOT NULL")
    List<Product> findDeActiveByBrand(@Param("brand") Brand brand);

    @Query("SELECT p FROM Product p " +
            "LEFT JOIN FETCH p.productImages pi " +
            "LEFT JOIN FETCH pi.image " +
            "LEFT JOIN FETCH p.productStatuses ps " +
            "LEFT JOIN FETCH ps.status " +
            "WHERE p.id = :id")
    Optional<Product> findByIdWithImagesAndStatuses(@Param("id") UUID id);

    @Modifying
    @Transactional
    @Query("UPDATE Product p SET p.deletedAt = :deletedAt WHERE p.id = :id")
    void softDeleteById(@Param("id") UUID id, @Param("deletedAt") LocalDateTime deletedAt);
}