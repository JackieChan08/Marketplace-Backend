package com.example.marketplace_backend.Repositories;

import com.example.marketplace_backend.Model.Phone.ProductMemory;
import com.example.marketplace_backend.Model.Phone.ProductMemoryAndProductColor;
import com.example.marketplace_backend.Model.ProductColor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ProductMemoryAndProductColorRepository extends JpaRepository<ProductMemoryAndProductColor, UUID> {
    @Query("SELECT pmpc FROM ProductMemoryAndProductColor pmpc WHERE pmpc.productColor.id = :colorId")
    List<ProductMemoryAndProductColor> findByProductColorId(@Param("colorId") UUID colorId);
}
