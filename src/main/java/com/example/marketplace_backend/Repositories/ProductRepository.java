package com.example.marketplace_backend.Repositories;

import com.example.marketplace_backend.Model.Category;
import com.example.marketplace_backend.Model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Repository
public interface ProductRepository extends JpaRepository<Product, UUID> {
    Product findByName(String name);
    @Query("SELECT p FROM Product p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%')) AND p.deletedAt = null")
    List<Product> findByNameContaining(@Param("name") String name);


    @Query("SELECT p FROM Product p WHERE p.category = :category and p.deletedAt != null")
    List<Product> findByCategory(@Param("category") Category category);

    @Query("SELECT p FROM Product p WHERE p.deletedAt = null ")
    List<Product> findAllActive();
    @Query("SELECT p FROM Product p WHERE p.deletedAt != null ")
    List<Product> findAllDeActive();

}
