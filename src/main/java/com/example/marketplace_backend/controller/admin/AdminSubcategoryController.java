package com.example.marketplace_backend.controller.admin;

import com.example.marketplace_backend.Model.Category;
import com.example.marketplace_backend.Model.FileEntity;
import com.example.marketplace_backend.Model.Intermediate_objects.SubcategoryImage;
import com.example.marketplace_backend.Model.Subcategory;
import com.example.marketplace_backend.Service.Impl.CategoryServiceImpl;
import com.example.marketplace_backend.Service.Impl.FileUploadService;
import com.example.marketplace_backend.Service.Impl.SubcategoryServiceImpl;
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
@RequestMapping("/api/admin/subcategories")
public class AdminSubcategoryController {

    private final SubcategoryServiceImpl  subcategoryService;
    private final FileUploadService  fileUploadService;
    private final CategoryServiceImpl categoryService;

    @GetMapping()
    public ResponseEntity<List<Subcategory>> getAllSubcategories() {
        return ResponseEntity.ok(subcategoryService.findAllActive());
    }

    @GetMapping("/inactive")
    public ResponseEntity<List<Subcategory>> getInactiveSubcategories() {
        return ResponseEntity.ok(subcategoryService.findAllDeActive());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Subcategory> getSubcategoryById(@PathVariable UUID id) {
        Optional<Subcategory> subcategory = subcategoryService.findById(id);
        return subcategory.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<Subcategory>> getSubcategoriesByCategory(@PathVariable UUID categoryId) {
        Optional<Category> categoryOpt = categoryService.findById(categoryId);
        return categoryOpt.map(category -> ResponseEntity.ok(subcategoryService.findByCategoryActive(category))).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/category/{categoryId}/inactive")
    public ResponseEntity<List<Subcategory>> getInactiveSubcategoriesByCategory(@PathVariable UUID categoryId) {
        Optional<Category> categoryOpt = categoryService.findById(categoryId);
        return categoryOpt.map(category -> ResponseEntity.ok(subcategoryService.findByCategoryDeActive(category))).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Long>> getSubcategoriesStats() {
        Map<String, Long> stats = new HashMap<>();
        stats.put("activeSubcategories", subcategoryService.countActiveSubcategories());
        stats.put("inactiveSubcategories", subcategoryService.countDeActiveSubcategories());
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/stats/category/{categoryId}")
    public ResponseEntity<Map<String, Long>> getSubcategoriesStatsByCategory(@PathVariable UUID categoryId) {
        Map<String, Long> stats = new HashMap<>();
        stats.put("activeSubcategories", subcategoryService.countActiveByCategoryId(categoryId));
        stats.put("inactiveSubcategories", subcategoryService.countDeActiveByCategoryId(categoryId));
        return ResponseEntity.ok(stats);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> softDeleteSubcategory(@PathVariable UUID id) {
        try {
            subcategoryService.softDelete(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/restore/{id}")
    public ResponseEntity<Subcategory> restoreSubcategory(@PathVariable UUID id) {
        try {
            subcategoryService.restore(id);
            Optional<Subcategory> subcategory = subcategoryService.findById(id);
            return subcategory.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping(value = "/create", consumes = {"multipart/form-data"})
    public ResponseEntity<Subcategory> createSubcategoryWithImages(
            @RequestParam String name,
            @RequestParam UUID categoryId,
            @RequestParam("images") List<MultipartFile> images
    ) throws Exception {

        Optional<Category> categoryOpt = categoryService.findById(categoryId);
        if (categoryOpt.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        Subcategory subcategory = new Subcategory();
        subcategory.setName(name);
        subcategory.setCategory(categoryOpt.get());
        subcategory.setDeletedAt(null);
        subcategory.setCreatedAt(LocalDateTime.now());

        subcategory = subcategoryService.save(subcategory);

        List<SubcategoryImage> subcategoryImages = new ArrayList<>();
        for (MultipartFile image : images) {
            FileEntity savedImage = fileUploadService.saveImage(image);
            SubcategoryImage subcategoryImage = SubcategoryImage.builder()
                    .subcategory(subcategory)
                    .image(savedImage)
                    .build();
            subcategoryImages.add(subcategoryImage);
        }

        subcategory.setSubcategoryImages(subcategoryImages);

        subcategory = subcategoryService.save(subcategory);

        return ResponseEntity.ok(subcategory);
    }

    @DeleteMapping("/{id}/permanent")
    public ResponseEntity<Void> permanentDeleteSubcategory(@PathVariable UUID id) {
        try {
            subcategoryService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/purge")
    public ResponseEntity<Void> purgeOldSubcategories(@RequestParam int daysOld) {
        try {
            LocalDateTime expirationDate = LocalDateTime.now().minusDays(daysOld);
            subcategoryService.purgeOldSubcategories(expirationDate);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping(value = "/edit/{id}", consumes = {"multipart/form-data"})
    public ResponseEntity<Subcategory> editSubcategory(
            @PathVariable UUID id,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) UUID categoryId,
            @RequestParam(name = "images", required = false) List<MultipartFile> images
    ) throws IOException {
        Subcategory subcategory = subcategoryService.getById(id);

        if (name != null && !name.isEmpty()) {
            subcategory.setName(name);
        }

        if (categoryId != null) {
            Optional<Category> categoryOpt = categoryService.findById(categoryId);
            categoryOpt.ifPresent(subcategory::setCategory);
        }

        if (images != null && !images.isEmpty()) {
            List<SubcategoryImage> newSubcategoryImages = images.stream()
                    .map(image -> {
                        try {
                            FileEntity savedImage = fileUploadService.saveImage(image);
                            return SubcategoryImage.builder()
                                    .subcategory(subcategory)
                                    .image(savedImage)
                                    .build();
                        } catch (IOException e) {
                            throw new RuntimeException("Ошибка при сохранении изображения", e);
                        }
                    })
                    .toList();

            if (subcategory.getSubcategoryImages() != null) {
                subcategory.getSubcategoryImages().addAll(newSubcategoryImages);
            } else {
                subcategory.setSubcategoryImages(newSubcategoryImages);
            }
        }

        subcategoryService.save(subcategory);
        return ResponseEntity.ok(subcategory);
    }

    @DeleteMapping("/{subcategoryId}/images/{imageId}")
    public ResponseEntity<Void> deleteSubcategoryImage(
            @PathVariable UUID subcategoryId,
            @PathVariable UUID imageId
    ) {
        try {
            Optional<Subcategory> subcategoryOpt = subcategoryService.findById(subcategoryId);
            if (subcategoryOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            Subcategory subcategory = subcategoryOpt.get();
            if (subcategory.getSubcategoryImages() != null) {
                subcategory.getSubcategoryImages().removeIf(img ->
                        img.getImage() != null && img.getImage().getId().equals(imageId)
                );
                subcategoryService.save(subcategory);
            }

            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


}
