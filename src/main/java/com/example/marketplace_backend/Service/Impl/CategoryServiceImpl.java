package com.example.marketplace_backend.Service.Impl;


import com.example.marketplace_backend.Model.Category;
import com.example.marketplace_backend.Model.Intermediate_objects.CategoryImage;
import com.example.marketplace_backend.Model.Intermediate_objects.ProductImage;
import com.example.marketplace_backend.Repositories.CategoryImageRepository;
import com.example.marketplace_backend.Repositories.CategoryRepository;
import com.example.marketplace_backend.Repositories.FileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
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

    @Transactional(readOnly = true)
    public List<Category> findAllDeActive() {
        return categoryRepository.findAllDeActive();
    }

    @Transactional(readOnly = true)
    public List<Category> findAllActive() {
        return categoryRepository.findAllActive();
    }

    @Transactional(readOnly = true)
    public Page<Category> findAll(Pageable pageable) {
        return categoryRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Optional<Category> findById(UUID categoryId) {
        return categoryRepository.findById(categoryId);
    }

    @Transactional(readOnly = true)
    public boolean existsByName(String name) {
        return categoryRepository.findByName(name) != null;
    }

    @Override
    public Category save(Category category) {
        if (category.getId() == null) {
            category.setCreatedAt(LocalDateTime.now());
        }
        else {
            category.setUpdatedAt(LocalDateTime.now());
        }
        return categoryRepository.save(category);
    }

    @Transactional
    public void softDelete(UUID id) {
        categoryRepository.softDeleteById(id, LocalDateTime.now());
    }

    @Transactional
    public void restore(UUID id) {
        Optional<Category> categoryOpt = categoryRepository.findById(id);
        if (categoryOpt.isPresent()) {
            Category category = categoryOpt.get();
            category.setDeletedAt(null);
            category.setUpdatedAt(LocalDateTime.now());
            categoryRepository.save(category);
        }
    }

    @Transactional
    public void purgeOldCategories(LocalDateTime expirationDate) {
        categoryRepository.purgeOldCategories(expirationDate);
    }

    @Override
    public void delete(UUID id) {
        try {
            List<CategoryImage> categoryImages = categoryImageRepository.findByCategoryId(id);

            for (CategoryImage categoryImage : categoryImages) {
                if (categoryImage.getImage() != null && categoryImage.getImage().getUniqueName() != null) {
                    fileUploadService.deleteImage(categoryImage.getImage().getUniqueName());
                }
            }

            categoryRepository.deleteById(id);

        } catch (Exception e) {
            throw new RuntimeException("Ошибка при удалении категории с ID: " + id, e);
        }
    }

    @Transactional(readOnly = true)
    public long countActiveCategories() {
        return findAllActive().size();
    }

    @Transactional(readOnly = true)
    public long countDeActiveCategories() {
        return findAllDeActive().size();
    }
}
