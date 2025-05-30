package com.example.marketplace_backend.Repositories;


import com.example.marketplace_backend.Model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    Category findByName(String name);
    @Query("SELECT c FROM Category c WHERE c.isDeleted = false")
    List<Category> findAllActive();

    @Query("SELECT c FROM Category c WHERE c.isDeleted = true ")
    List<Category> findAllDeActive();
}
