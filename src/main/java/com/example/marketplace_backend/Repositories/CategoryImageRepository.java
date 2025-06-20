package com.example.marketplace_backend.Repositories;


import com.example.marketplace_backend.Model.Intermediate_objects.CategoryImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CategoryImageRepository extends JpaRepository<CategoryImage, UUID> {
    List<CategoryImage> findByCategoryId(UUID categoryId);
}
