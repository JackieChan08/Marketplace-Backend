package com.example.marketplace_backend.Repositories;

import com.example.marketplace_backend.Model.Brand;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface BrandRepository extends JpaRepository<Brand, UUID> {
    @Query("SELECT b FROM Brand b WHERE b.deletedAt = null")
    List<Brand> findAllActive();
}
