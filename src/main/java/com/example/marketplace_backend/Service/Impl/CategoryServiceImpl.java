package com.example.marketplace_backend.Service.Impl;

import com.example.marketplace_backend.DTO.Requests.models.CategoryRequest;
import com.example.marketplace_backend.DTO.Responses.models.CategoryResponse;
import com.example.marketplace_backend.Model.Category;
import com.example.marketplace_backend.Model.FileEntity;
import com.example.marketplace_backend.Model.Intermediate_objects.CategoryIcon;
import com.example.marketplace_backend.Model.Intermediate_objects.CategoryImage;
import com.example.marketplace_backend.Repositories.CategoryIconRepository;
import com.example.marketplace_backend.Repositories.CategoryImageRepository;
import com.example.marketplace_backend.Repositories.CategoryRepository;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private final CategoryIconRepository categoryIconRepository;
    private final Logger log = LoggerFactory.getLogger(CategoryServiceImpl.class);

    @Autowired
    public CategoryServiceImpl(CategoryRepository categoryRepository,
                               CategoryImageRepository categoryImageRepository,
                               FileUploadService fileUploadService,
                               ConverterService converterService,
                               CategoryIconRepository categoryIconRepository) {
        super(categoryRepository);
        this.categoryRepository = categoryRepository;
        this.categoryImageRepository = categoryImageRepository;
        this.fileUploadService = fileUploadService;
        this.converterService = converterService;
        this.categoryIconRepository = categoryIconRepository;
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
    @Transactional
    public Page<Category> findAllActive(Pageable pageable) {
        return categoryRepository.findAllActive(pageable);
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

        // Сохранение изображения
        if (request.getImage() != null && !request.getImage().isEmpty()) {
            FileEntity savedImage = fileUploadService.saveImage(request.getImage());

            CategoryImage categoryImage = CategoryImage.builder()
                    .category(category)
                    .image(savedImage)
                    .build();

            List<CategoryImage> images = new ArrayList<>();
            images.add(categoryImage);
            category.setCategoryImages(images);

            categoryImageRepository.save(categoryImage);
        }

        // Сохранение иконки
        if (request.getIcon() != null && !request.getIcon().isEmpty()) {
            FileEntity savedIcon = fileUploadService.saveImage(request.getIcon());

            CategoryIcon categoryIcon = CategoryIcon.builder()
                    .category(category)
                    .icon(savedIcon)
                    .build();

            List<CategoryIcon> icons = new ArrayList<>();
            icons.add(categoryIcon);
            category.setCategoryIcons(icons);

            categoryIconRepository.save(categoryIcon);
        }

        category = save(category);
        return category;
    }

    @Transactional
    public Category updateCategory(UUID id, CategoryRequest request) throws IOException {
        Category category = getById(id);

        if (request.getName() != null && !request.getName().isEmpty()) {
            category.setName(request.getName());
        }

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

        if (request.getIcon() != null && !request.getIcon().isEmpty()) {

            FileEntity savedIcon = fileUploadService.saveImage(request.getIcon());

            CategoryIcon categoryIcon = CategoryIcon.builder()
                    .category(category)
                    .icon(savedIcon)
                    .build();

            if (category.getCategoryIcons() == null) {
                category.setCategoryIcons(new ArrayList<>());
            } else {
                category.getCategoryIcons().clear();
            }

            category.getCategoryIcons().add(categoryIcon);
        }

        category.setUpdatedAt(LocalDateTime.now());
        return save(category);
    }

    @Transactional
    public void deleteCategoryImage(UUID categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new EntityNotFoundException("Category not found: " + categoryId));

        List<CategoryImage> images = new ArrayList<>(category.getCategoryImages());

        if (!images.isEmpty()) {
            for (CategoryImage categoryImage : images) {
                // Удаление физического файла
                if (categoryImage.getImage() != null && categoryImage.getImage().getUniqueName() != null) {
                    try {
                        boolean deleted = fileUploadService.deleteImage(categoryImage.getImage().getUniqueName());
                        if (deleted) {
                            log.info("Удален файл изображения: {}", categoryImage.getImage().getUniqueName());
                        } else {
                            log.warn("Не удалось удалить файл изображения: {}", categoryImage.getImage().getUniqueName());
                        }
                    } catch (Exception e) {
                        log.error("Ошибка при удалении файла изображения: {}", categoryImage.getImage().getUniqueName(), e);
                    }
                }

                // Удаление из базы данных
                categoryImageRepository.delete(categoryImage);
            }

            // Очистка коллекции и сохранение изменений
            category.getCategoryImages().clear();
            category.setUpdatedAt(LocalDateTime.now());
            categoryRepository.save(category);

            log.info("Изображения категории {} успешно удалены", categoryId);
        } else {
            log.info("У категории {} нет изображений для удаления", categoryId);
        }
    }

    @Transactional
    public void deleteCategoryIcon(UUID categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new EntityNotFoundException("Category not found: " + categoryId));

        List<CategoryIcon> icons = new ArrayList<>(category.getCategoryIcons());

        if (!icons.isEmpty()) {
            for (CategoryIcon categoryIcon : icons) {
                // Удаление физического файла
                if (categoryIcon.getIcon() != null && categoryIcon.getIcon().getUniqueName() != null) {
                    try {
                        boolean deleted = fileUploadService.deleteImage(categoryIcon.getIcon().getUniqueName());
                        if (deleted) {
                            log.info("Удален файл иконки: {}", categoryIcon.getIcon().getUniqueName());
                        } else {
                            log.warn("Не удалось удалить файл иконки: {}", categoryIcon.getIcon().getUniqueName());
                        }
                    } catch (Exception e) {
                        log.error("Ошибка при удалении файла иконки: {}", categoryIcon.getIcon().getUniqueName(), e);
                    }
                }

                // Удаление из базы данных
                categoryIconRepository.delete(categoryIcon);
            }

            // Очистка коллекции и сохранение изменений
            category.getCategoryIcons().clear();
            category.setUpdatedAt(LocalDateTime.now());
            categoryRepository.save(category);

            log.info("Иконки категории {} успешно удалены", categoryId);
        } else {
            log.info("У категории {} нет иконок для удаления", categoryId);
        }
    }


    // Улучшенный метод для удаления старых иконок
    private void deleteOldCategoryIcon(Category category) {
        if (category.getCategoryIcons() != null && !category.getCategoryIcons().isEmpty()) {
            for (CategoryIcon categoryIcon : category.getCategoryIcons()) {
                if (categoryIcon.getIcon() != null && categoryIcon.getIcon().getUniqueName() != null) {
                    try {
                        boolean deleted = fileUploadService.deleteImage(categoryIcon.getIcon().getUniqueName());
                        if (deleted) {
                            log.info("Успешно удален файл иконки: {}", categoryIcon.getIcon().getUniqueName());
                        } else {
                            log.warn("Не удалось удалить файл иконки: {}", categoryIcon.getIcon().getUniqueName());
                        }
                    } catch (Exception e) {
                        log.error("Ошибка при удалении файла иконки: {}", categoryIcon.getIcon().getUniqueName(), e);
                    }
                }
            }
        }
    }

    // Улучшенный метод для удаления старых изображений
    private void deleteOldCategoryImage(Category category) {
        if (category.getCategoryImages() != null && !category.getCategoryImages().isEmpty()) {
            for (CategoryImage categoryImage : category.getCategoryImages()) {
                if (categoryImage.getImage() != null && categoryImage.getImage().getUniqueName() != null) {
                    try {
                        boolean deleted = fileUploadService.deleteImage(categoryImage.getImage().getUniqueName());
                        if (deleted) {
                            log.info("Успешно удален файл изображения: {}", categoryImage.getImage().getUniqueName());
                        } else {
                            log.warn("Не удалось удалить файл изображения: {}", categoryImage.getImage().getUniqueName());
                        }
                    } catch (Exception e) {
                        log.error("Ошибка при удалении файла изображения: {}", categoryImage.getImage().getUniqueName(), e);
                    }
                }
            }
        }
    }

    public Page<Category> findCategoriesByPriority(Pageable pageable) {
        return categoryRepository.findCategoriesByPriority(pageable);
    }

}