package com.example.marketplace_backend.Repositories;

import com.example.marketplace_backend.Model.ProductVariant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ProductVariantRepository extends JpaRepository<ProductVariant, UUID> {
    @Query("SELECT pv FROM ProductVariant pv WHERE pv.product.id = :productId")
    List<ProductVariant> findByProductId(@Param("productId") UUID productId);

    @Modifying
    @Query("DELETE FROM ProductVariant pv WHERE pv.product.id = :productId")
    void deleteByProductId(@Param("productId") UUID productId);

    @Query("SELECT pv FROM ProductVariant pv WHERE pv.color.id = :colorId")
    List<ProductVariant> findByColorId(@Param("colorId") UUID colorId);

    @Query("SELECT pv FROM ProductVariant pv WHERE pv.phoneSpec.id = :phoneSpecId")
    List<ProductVariant> findByPhoneSpecId(@Param("phoneSpecId") UUID phoneSpecId);

    @Query("SELECT pv FROM ProductVariant pv WHERE pv.laptopSpec.id = :laptopSpecId")
    List<ProductVariant> findByLaptopSpecId(@Param("laptopSpecId") UUID laptopSpecId);


}
