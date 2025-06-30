package com.example.marketplace_backend.controller.admin;

import com.example.marketplace_backend.Model.Category;
import com.example.marketplace_backend.Model.FileEntity;
import com.example.marketplace_backend.Model.Intermediate_objects.CategoryImage;
import com.example.marketplace_backend.Service.Impl.CategoryServiceImpl;
import com.example.marketplace_backend.Service.Impl.FileUploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/categories")
public class AdminCategoryController {
    private final CategoryServiceImpl  categoryService;
    private final FileUploadService fileUploadService;

    @GetMapping()
    public ResponseEntity<List<Category>> getAllCategories() {
        return ResponseEntity.ok(categoryService.findAllActive());
    }

    @GetMapping("/inactive")
    public ResponseEntity<List<Category>> getInactiveCategories() {
        return ResponseEntity.ok(categoryService.findAllDeActive());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Category> getCategoryById(@PathVariable UUID id) {
        Optional<Category> category = categoryService.findById(id);
        return category.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/exists/{name}")
    public ResponseEntity<Boolean> categoryExistsByName(@PathVariable String name) {
        return ResponseEntity.ok(categoryService.existsByName(name));
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Long>> getCategoriesStats() {
        Map<String, Long> stats = new HashMap<>();
        stats.put("activeCategories", categoryService.countActiveCategories());
        stats.put("inactiveCategories", categoryService.countDeActiveCategories());
        return ResponseEntity.ok(stats);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> softDeleteCategory(@PathVariable UUID id) {
        try {
            categoryService.softDelete(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/restore/{id}")
    public ResponseEntity<Category> restoreCategory(@PathVariable UUID id) {
        try {
            categoryService.restore(id);
            Optional<Category> category = categoryService.findById(id);
            return category.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping(value = "/create", consumes = {"multipart/form-data"})
    public ResponseEntity<Category> createCategoryWithImages(
            @RequestParam String name,
            @RequestParam("images") List<MultipartFile> images
    ) throws Exception {

        Category category = new Category();
        category.setName(name);
        category.setDeletedAt(null);
        category.setCreatedAt(LocalDateTime.now());

        category = categoryService.save(category);

        List<CategoryImage> categoryImages = new ArrayList<>();
        for (MultipartFile image : images) {
            FileEntity savedImage = fileUploadService.saveImage(image);
            CategoryImage categoryImage = CategoryImage.builder()
                    .category(category)
                    .image(savedImage)
                    .build();
            categoryImages.add(categoryImage);
        }

        category.setCategoryImages(categoryImages);

        category = categoryService.save(category);

        return ResponseEntity.ok(category);
    }


    @DeleteMapping("/{id}/permanent")
    public ResponseEntity<Void> permanentDeleteCategory(@PathVariable UUID id) {
        try {
            categoryService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/purge")
    public ResponseEntity<Void> purgeOldCategories(@RequestParam int daysOld) {
        try {
            LocalDateTime expirationDate = LocalDateTime.now().minusDays(daysOld);
            categoryService.purgeOldCategories(expirationDate);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping(value = "/edit/{id}", consumes = {"multipart/form-data"})
    public ResponseEntity<Category> editCategory(
            @PathVariable UUID id,
            @RequestParam(required = false) String name,
            @RequestParam(name = "images", required = false) List<MultipartFile> images
    ) throws IOException {
        Category category = categoryService.getById(id);

        if (name != null && !name.isEmpty()) {
            category.setName(name);
        }

        if (images != null && !images.isEmpty()) {
            List<CategoryImage> newCategoryImages = images.stream()
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

        categoryService.save(category);
        return ResponseEntity.ok(category);
    }

    @DeleteMapping("/{categoryId}/images/{imageId}")
    public ResponseEntity<Void> deleteCategoryImage(
            @PathVariable UUID categoryId,
            @PathVariable UUID imageId
    ) {
        try {
            Optional<Category> categoryOpt = categoryService.findById(categoryId);
            if (categoryOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            Category category = categoryOpt.get();
            if (category.getCategoryImages() != null) {
                category.getCategoryImages().removeIf(img ->
                        img.getImage() != null && img.getImage().getId().equals(imageId)
                );
                categoryService.save(category);
            }

            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
