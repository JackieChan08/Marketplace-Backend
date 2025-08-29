package com.example.marketplace_backend.Repositories;

import com.example.marketplace_backend.Model.Intermediate_objects.ProductColorImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Repository
public interface ProductColorImageRepository extends JpaRepository<ProductColorImage, UUID> {

    @Query("SELECT pci FROM ProductColorImage pci WHERE pci.color.id = :colorId")
    List<ProductColorImage> findByColorId(@Param("colorId") UUID colorId);

    @Modifying
    @Transactional
    @Query("DELETE FROM ProductColorImage pci WHERE pci.color.id = :colorId")
    void deleteByColorId(@Param("colorId") UUID colorId);
}