package com.example.marketplace_backend.controller.admin;

import com.example.marketplace_backend.DTO.Requests.models.SubcategoryRequest;
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
            @RequestParam SubcategoryRequest request
            ) throws Exception {

        return subcategoryService.createSubcategory(request);
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
            @RequestParam() SubcategoryRequest request
    ) throws IOException {
        return subcategoryService.updateSubcategory(id, request);
    }
    @DeleteMapping("/{subcategoryId}/images/{imageId}")
    public ResponseEntity<Void> deleteSubcategoryImage(
            @PathVariable UUID subcategoryId,
            @PathVariable UUID imageId
    ) {
        try {
            boolean deleted = subcategoryService.deleteSubcategoryImage(subcategoryId, imageId);

            if (!deleted) {
                return ResponseEntity.notFound().build();
            }

            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }



}
