package com.example.marketplace_backend.Service.Impl;


import com.example.marketplace_backend.DTO.Requests.models.CategoryRequest;
import com.example.marketplace_backend.Model.Category;
import com.example.marketplace_backend.Model.FileEntity;
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
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
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
    public void purgeOldCategories() {
        List<Category> categoriesToDelete = categoryRepository.findAllDeActive();
        categoryRepository.deleteAll(categoriesToDelete);
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

    public Category createCategory(CategoryRequest request) throws IOException {

        Category category = new Category();
        category.setName(request.getName());
        category.setDeletedAt(null);
        category.setCreatedAt(LocalDateTime.now());

        category = save(category);

        List<CategoryImage> categoryImages = new ArrayList<>();
        for (MultipartFile image : request.getImages()) {
            FileEntity savedImage = fileUploadService.saveImage(image);
            CategoryImage categoryImage = CategoryImage.builder()
                    .category(category)
                    .image(savedImage)
                    .build();
            categoryImages.add(categoryImage);
        }

        category.setCategoryImages(categoryImages);

        category = save(category);
        return category;

    }

    public Category updateCategory(UUID id,CategoryRequest request) throws IOException {

        Category category = getById(id);

        if (request.getName() != null && !request.getName().isEmpty()) {
            category.setName(request.getName());
        }

        if (request.getImages() != null && !request.getImages().isEmpty()) {
            List<CategoryImage> newCategoryImages = request.getImages().stream()
                    .map(image -> {
                        try {
                            FileEntity savedImage = fileUploadService.saveImage(image);
                            return CategoryImage.builder()
                                    .category(category)
                                    .image(savedImage)
                                    .build();
                        } catch (IOException e) {
                            throw new RuntimeException("Ошибка при сохранении изображения", e);
                        }
                    })
                    .toList();

            if (category.getCategoryImages() != null) {
                category.getCategoryImages().addAll(newCategoryImages);
            } else {
                category.setCategoryImages(newCategoryImages);
            }
        }

        return save(category);
    }


    public boolean deleteCategoryImage(UUID categoryId, UUID imageId) {
        Optional<Category> categoryOpt = categoryRepository.findById(categoryId);
        if (categoryOpt.isEmpty()) {
            return false;
        }

        Category category = categoryOpt.get();

        boolean removed = false;
        if (category.getCategoryImages() != null) {
            removed = category.getCategoryImages().removeIf(img ->
                    img.getImage() != null && img.getImage().getId().equals(imageId)
            );
        }

        if (removed) {
            categoryRepository.save(category);
        }

        return removed;
    }
}
