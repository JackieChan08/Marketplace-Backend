package com.example.marketplace_backend.Repositories;

import com.example.marketplace_backend.Model.Intermediate_objects.CategoryIcon;
import com.example.marketplace_backend.Model.Intermediate_objects.CategoryImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CategoryIconRepository extends JpaRepository<CategoryIcon, UUID> {
    List<CategoryIcon> findByCategoryId(UUID categoryId);

    @Modifying
    @Query("DELETE FROM CategoryIcon ci WHERE ci.category.id = :categoryId")
    void deleteByCategoryId(@Param("categoryId") UUID categoryId);
}
