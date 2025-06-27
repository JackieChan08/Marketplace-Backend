package com.example.marketplace_backend.Repositories;

import com.example.marketplace_backend.Model.Brand;
import com.example.marketplace_backend.Model.Category;
import com.example.marketplace_backend.Model.Product;
import com.example.marketplace_backend.Model.Subcategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ProductRepository extends JpaRepository<Product, UUID> {
    Product findByName(String name);

    @Query("SELECT p FROM Product p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :query, '%'))")
    Page<Product> findByNameContaining(@Param("query") String name, Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.deletedAt IS NULL")
    List<Product> findAllActive();

    @Query("SELECT p FROM Product p WHERE p.deletedAt IS NOT NULL")
    List<Product> findAllDeActive();

    @Query("SELECT p FROM Product p WHERE p.subcategory = :subcategory and p.deletedAt IS NULL")
    List<Product> findBySubcategoryAndDeletedAtIsNull(@Param("subcategory") Subcategory subcategory);

    @Query("SELECT p FROM Product p WHERE p.subcategory = :cubategory and p.deletedAt IS NOT NULL")
    List<Product> findBySubcategoryAndDeletedAtIsNotNull(@Param("subcategory") Subcategory subcategory);

    @Query("SELECT p FROM Product p WHERE p.brand = :brand and p.deletedAt IS NULL")
    List<Product> findByBrandAndDeletedAtIsNull(@Param("brand") Brand brand);

    @Query("SELECT p FROM Product p WHERE p.brand = :brand and p.deletedAt IS NOT NULL")
    List<Product> findByBrandAndDeletedAtIsNotNull(@Param("brand") Brand brand);
}
