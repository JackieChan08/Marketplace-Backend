package com.example.marketplace_backend.Repositories;

import com.example.marketplace_backend.Model.Product;
import com.example.marketplace_backend.Model.ProductParameters;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProductParametersRepository extends JpaRepository<ProductParameters, UUID> {
    List<ProductParameters> findByProductId(@Param("productId") UUID productId);

    Optional<ProductParameters> findByNameAndProductId(@Param("name") String name, @Param("productId") UUID productId);

    @Query("SELECT COUNT(pp) FROM ProductParameters pp WHERE pp.product.id = :productId")
    long countByProductId(@Param("productId") UUID productId);

    @Modifying
    @Transactional
    @Query("DELETE FROM ProductParameters pp WHERE pp.product.id = :productId")
    void deleteByProductId(@Param("productId") UUID productId);

}
