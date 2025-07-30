package com.example.marketplace_backend.Repositories;

import com.example.marketplace_backend.Model.ProductColor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ProductColorRepository extends JpaRepository<ProductColor, UUID> {

    @Query("SELECT pc FROM ProductColor pc WHERE pc.product.id = :productId")
    List<ProductColor> findByProductId(@Param("productId") UUID productId);

    void deleteByProductId(UUID productId);
}