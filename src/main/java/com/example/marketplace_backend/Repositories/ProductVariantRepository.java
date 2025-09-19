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

    // По productId
    @Query("SELECT pv FROM ProductVariant pv WHERE pv.product.id = :productId")
    List<ProductVariant> findByProductId(@Param("productId") UUID productId);

    @Modifying
    @Query("DELETE FROM ProductVariant pv WHERE pv.product.id = :productId")
    void deleteByProductId(@Param("productId") UUID productId);

    // По colorId
    @Query("SELECT pv FROM ProductVariant pv WHERE pv.color.id = :colorId")
    List<ProductVariant> findByColorId(@Param("colorId") UUID colorId);

    // По phoneSpecId
    @Query("SELECT pv FROM ProductVariant pv WHERE pv.phoneSpec.id = :phoneSpecId")
    ProductVariant findByPhoneSpecId(@Param("phoneSpecId") UUID phoneSpecId);

    // По laptopSpecId
    @Query("SELECT pv FROM ProductVariant pv WHERE pv.laptopSpec.id = :laptopSpecId")
    ProductVariant findByLaptopSpecId(@Param("laptopSpecId") UUID laptopSpecId);

    // По tableSpecId
    @Query("SELECT pv FROM ProductVariant pv WHERE pv.tableSpec.id = :tableSpecId")
    ProductVariant findByTableSpecId(@Param("tableSpecId") UUID tableSpecId);

    // По watchSpecId
    @Query("SELECT pv FROM ProductVariant pv WHERE pv.watchSpec.id = :watchSpecId")
    ProductVariant findByWatchSpecId(@Param("watchSpecId") UUID watchSpecId);
}

