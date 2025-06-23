package com.example.marketplace_backend.Repositories;

import com.example.marketplace_backend.Model.Subcategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface SubcategoryRepository extends JpaRepository<Subcategory, UUID> {
    @Query("SELECT s FROM Subcategory s WHERE s.id = :id AND s.deletedAt IS NOT NULL")
    Optional<Subcategory> findDeletedById(@Param("id") UUID id);

}
