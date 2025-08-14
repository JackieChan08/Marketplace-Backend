package com.example.marketplace_backend.Repositories;

import com.example.marketplace_backend.Model.ProductMemory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ProductMemoryRepository extends JpaRepository<ProductMemory, UUID> {
    List<ProductMemory> findByColorId(UUID colorId);

    void deleteByColor_Product_Id(UUID productId);
}
