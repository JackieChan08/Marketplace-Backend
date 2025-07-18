package com.example.marketplace_backend.Service.Impl;

import com.example.marketplace_backend.DTO.Requests.models.CategoryRequest;
import com.example.marketplace_backend.DTO.Responses.models.CategoryResponse;
import com.example.marketplace_backend.Model.Category;
import com.example.marketplace_backend.Model.FileEntity;
import com.example.marketplace_backend.Model.Intermediate_objects.CategoryImage;
import com.example.marketplace_backend.Repositories.CategoryImageRepository;
import com.example.marketplace_backend.Repositories.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private final ConverterService converterService;

    @Autowired
    public CategoryServiceImpl(CategoryRepository categoryRepository,
                               CategoryImageRepository categoryImageRepository,
                               FileUploadService fileUploadService,
                               ConverterService converterService) {
        super(categoryRepository);
        this.categoryRepository = categoryRepository;
        this.categoryImageRepository = categoryImageRepository;
        this.fileUploadService = fileUploadService;
        this.converterService = converterService;
    }

    public Category findByName(String name) {
        return categoryRepository.findByName(name);
    }

    @Transactional(readOnly = true)
    public List<Category> findAllDeActive() {
        return categoryRepository.findAllDeActive();
    }

    @Transactional
    public Page<Category> findAllDeActive(Pageable pageable) {
        return categoryRepository.findAllDeActive(pageable);
    }

    @Transactional(readOnly = true)
    public List<Category> findAllActive() {
        return categoryRepository.findAllActive();
    }

    @Transactional(readOnly = true)
    public Page<CategoryResponse> findAll(Pageable pageable) {
        return categoryRepository.findAll(pageable)
                .map(converterService::convertToCategoryResponse);
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

    @Transactional
    public Category createCategory(CategoryRequest request) throws IOException {
        Category category = new Category();
        category.setName(request.getName());
        category.setDeletedAt(null);
        category.setCreatedAt(LocalDateTime.now());
        category.setPriority(request.isPriority());

        category = save(category);

        FileEntity savedImage = fileUploadService.saveImage(request.getImage());

        CategoryImage categoryImage = CategoryImage.builder()
                .category(category)
                .image(savedImage)
                .build();

        List<CategoryImage> images = new ArrayList<>();
        images.add(categoryImage);
        category.setCategoryImages(images);

        category = save(category);
        return category;
    }

    @Transactional
    public Category updateCategory(UUID id, CategoryRequest request) throws IOException {
        Category category = getById(id);

        if (request.getName() != null && !request.getName().isEmpty()) {
            category.setName(request.getName());
        }

        if (request.isPriority()) {
            category.setPriority(true);
        }

        // Обновляем приоритет
        category.setPriority(request.isPriority());

        if (request.getImage() != null && !request.getImage().isEmpty()) {


            // Сохраняем новое изображение
            FileEntity savedImage = fileUploadService.saveImage(request.getImage());

            // Создаем новое изображение категории
            CategoryImage categoryImage = CategoryImage.builder()
                    .category(category)
                    .image(savedImage)
                    .build();

            // Инициализируем коллекцию если она null
            if (category.getCategoryImages() == null) {
                category.setCategoryImages(new ArrayList<>());
            } else {
                // Очищаем старые изображения
                category.getCategoryImages().clear();
            }

            // Добавляем новое изображение
            category.getCategoryImages().add(categoryImage);
        }

        category.setUpdatedAt(LocalDateTime.now());
        return save(category);
    }

    @Transactional
    public void deleteCategoryImage(UUID categoryId) {
        Category category = getById(categoryId);

        if (category.getCategoryImages() != null && !category.getCategoryImages().isEmpty()) {
            // Удаляем изображение из файловой системы
            deleteOldCategoryImage(category);

            // Очищаем коллекцию изображений
            category.getCategoryImages().clear();
            save(category);
        }
    }

    private void deleteOldCategoryImage(Category category) {
        if (category.getCategoryImages() != null && !category.getCategoryImages().isEmpty()) {
            CategoryImage oldImage = category.getCategoryImages().get(0);
            if (oldImage.getImage() != null && oldImage.getImage().getUniqueName() != null) {
                try {
                    fileUploadService.deleteImage(oldImage.getImage().getUniqueName());
                } catch (Exception e) {
                    System.err.println("Ошибка при удалении старого изображения: " + e.getMessage());
                }
            }
        }
    }
}