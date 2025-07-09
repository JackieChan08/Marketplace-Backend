package com.example.marketplace_backend.controller.admin;

import com.example.marketplace_backend.Model.Category;
import com.example.marketplace_backend.Service.Impl.CategoryServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.*;
import java.io.IOException;
import com.example.marketplace_backend.DTO.Requests.models.CategoryRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/categories")
public class AdminCategoryController {
    private final CategoryServiceImpl categoryService;

    @GetMapping
    public ResponseEntity<Page<Category>> getAllCategories(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(categoryService.findAll(pageable));
    }

    @GetMapping("/inactive")
    public ResponseEntity<List<Category>> getInactiveCategories() {
        return ResponseEntity.ok(categoryService.findAllDeActive());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Category> getCategoryById(@PathVariable UUID id) {
        Optional<Category> category = categoryService.findById(id);
        return category.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
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
            return category.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping(value = "/create", consumes = {"multipart/form-data"})
    public ResponseEntity<Category> createCategoryWithImages(@ModelAttribute CategoryRequest request) throws Exception {
        return ResponseEntity.ok(categoryService.createCategory(request));
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
    public ResponseEntity<Void> purgeCategories() {
        try {
            categoryService.purgeOldCategories();
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping(value = "/edit/{id}", consumes = {"multipart/form-data"})
    public ResponseEntity<Category> editCategory(
            @PathVariable UUID id,
            @ModelAttribute CategoryRequest request
    ) throws IOException {
        return ResponseEntity.ok(categoryService.updateCategory(id, request));
    }

    @DeleteMapping("/{categoryId}/images/{imageId}")
    public ResponseEntity<Void> deleteCategoryImage(
            @PathVariable UUID categoryId,
            @PathVariable UUID imageId
    ) {
        try {
            boolean deleted = categoryService.deleteCategoryImage(categoryId, imageId);
            if (!deleted) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}