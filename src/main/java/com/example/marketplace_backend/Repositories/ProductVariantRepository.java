package com.example.marketplace_backend.Repositories;

import com.example.marketplace_backend.Model.ProductVariant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
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

    @Query("SELECT COUNT(DISTINCT v.color) FROM ProductVariant v WHERE v.product.id = :productId")
    long countDistinctColorsByProductId(@Param("productId") UUID productId);

    // Мягкое удаление / восстановление
    @Modifying
    @Transactional
    @Query("UPDATE ProductVariant p SET p.deletedAt = :deletedAt WHERE p.id = :id")
    void softDeleteById(@Param("id") UUID id, @Param("deletedAt") LocalDateTime deletedAt);

    // Удаление всех мягко удаленных записей
    @Modifying
    @Transactional
    @Query("DELETE FROM ProductVariant p WHERE p.deletedAt IS NOT NULL")
    void deleteAllSoftDeleted();

    // Поиск всех мягко удаленных записей
    @Query("SELECT p FROM ProductVariant p WHERE p.deletedAt IS NOT NULL")
    List<ProductVariant> findAllSoftDeleted();

    // Поиск активных записей (не удаленных)
    @Query("SELECT p FROM ProductVariant p WHERE p.deletedAt IS NULL")
    List<ProductVariant> findAllActive();

    @Modifying
    @Query("update ProductVariant pv set pv.deletedAt = CURRENT_TIMESTAMP where pv.product.id = :productId")
    void softDeleteByProductId(@Param("productId") UUID productId);

}