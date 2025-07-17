package com.example.marketplace_backend.controller.admin;

import com.example.marketplace_backend.DTO.Requests.models.BrandRequest;
import com.example.marketplace_backend.DTO.Responses.models.CategoryResponse;
import com.example.marketplace_backend.Model.Brand;
import com.example.marketplace_backend.Model.Category;
import com.example.marketplace_backend.Service.Impl.CategoryServiceImpl;
import com.example.marketplace_backend.Service.Impl.ConverterService;
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
    private final ConverterService converterService;

    @GetMapping
    public ResponseEntity<Page<CategoryResponse>> getAllCategories(
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
    public ResponseEntity<?> createCategoryWithImages(@ModelAttribute CategoryRequest request)
            throws Exception {
        if (request.getImage() == null || request.getImage().isEmpty()) {
            return ResponseEntity.badRequest().body("Изображение обязательно для создания категории.");
        }

        CategoryResponse category = converterService.convertToCategoryResponse(categoryService.createCategory(request));
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
    public ResponseEntity<Void> purgeCategories() {
        try {
            categoryService.purgeOldCategories();
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping(value = "/edit/{id}", consumes = {"multipart/form-data"})
    public ResponseEntity<CategoryResponse> editCategory(
            @PathVariable UUID id,
            @ModelAttribute CategoryRequest request
    ) throws IOException {
        CategoryResponse categoryResponse = converterService.convertToCategoryResponse(categoryService.updateCategory(id, request));
        return ResponseEntity.ok(categoryResponse);
    }

    @DeleteMapping("/{categoryId}/images/{imageId}")
    public ResponseEntity<Void> deleteCategoryImage(
            @PathVariable UUID categoryId
    ) {
        try {
            categoryService.deleteCategoryImage(categoryId);
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}