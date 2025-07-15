package com.example.marketplace_backend.Repositories;

import com.example.marketplace_backend.Model.Intermediate_objects.ProductStatuses;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Repository
public interface ProductStatusRepository extends JpaRepository<ProductStatuses, UUID> {

    @Query("SELECT ps FROM ProductStatuses ps WHERE ps.product.id = :productId")
    List<ProductStatuses> findByProductId(@Param("productId") UUID productId);

    @Modifying
    @Transactional
    @Query("DELETE FROM ProductStatuses ps WHERE ps.product.id = :productId")
    void deleteByProductId(@Param("productId") UUID productId);
}