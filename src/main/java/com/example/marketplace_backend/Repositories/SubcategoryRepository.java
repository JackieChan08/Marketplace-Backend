package com.example.marketplace_backend.Repositories;

import com.example.marketplace_backend.Model.Product;
import com.example.marketplace_backend.Model.Subcategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubcategoryRepository extends JpaRepository<Subcategory, Long> {
    @Query("SELECT s FROM Subcategory s WHERE s.isDeleted = false")
    List<Subcategory> findAllActive();
}
