package com.example.marketplace_backend.Repositories;

import com.example.marketplace_backend.Model.Intermediate_objects.SubcategoryImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SubcategoryImageRepository extends JpaRepository<SubcategoryImage, UUID> {
    List<SubcategoryImage> findBySubcategoryId(UUID id);
}
