package com.example.marketplace_backend.Service.Impl;


import com.example.marketplace_backend.Model.Category;
import com.example.marketplace_backend.Model.Intermediate_objects.CategoryImage;
import com.example.marketplace_backend.Model.Intermediate_objects.ProductImage;
import com.example.marketplace_backend.Repositories.CategoryImageRepository;
import com.example.marketplace_backend.Repositories.CategoryRepository;
import com.example.marketplace_backend.Repositories.FileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class CategoryServiceImpl extends BaseServiceImpl<Category, UUID> {
    private final CategoryRepository categoryRepository;
    private final CategoryImageRepository categoryImageRepository;
    private final FileUploadService  fileUploadService;

    @Autowired
    public CategoryServiceImpl(CategoryRepository categoryRepository, CategoryImageRepository categoryImageRepository, FileUploadService fileUploadService) {
        super(categoryRepository);
        this.categoryRepository = categoryRepository;
        this.categoryImageRepository = categoryImageRepository;
        this.fileUploadService = fileUploadService;
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

    @Override
    public void delete(UUID id) {
        List<CategoryImage>  categoryImages = categoryImageRepository.findByCategoryId(id);
        for (CategoryImage categoryImage : categoryImages) {
            fileUploadService.deleteImage(categoryImage.getImage().getUniqueName());
        }
        categoryRepository.deleteById(id);
    }
}
