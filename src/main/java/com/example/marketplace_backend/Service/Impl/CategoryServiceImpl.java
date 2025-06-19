package com.example.marketplace_backend.Service.Impl;


import com.example.marketplace_backend.Model.Category;
import com.example.marketplace_backend.Repositories.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class CategoryServiceImpl extends BaseServiceImpl<Category, UUID> {
    private final CategoryRepository categoryRepository;

    @Autowired
    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        super(categoryRepository);
        this.categoryRepository = categoryRepository;
    }

    public Category findByName(String name) {
        return categoryRepository.findByName(name);
    }

    public List<Category> findAllDeActive() {
        return categoryRepository.findAllDeActive();
    }
    public List<Category> findAllActive() {
        return categoryRepository.findAllActive();
    }
    public List<Category> findAllWithProducts() {
        return categoryRepository.findAll();
    }

    public Category findActiveCategoryById(UUID id) {
        Category category = categoryRepository.getById(id);
        if (category.getDeletedAt() == null) {
            return category;
        }
        return null;
    }

    public Optional<Category> findById(UUID categoryId) {
        return categoryRepository.findById(categoryId);
    }
}
