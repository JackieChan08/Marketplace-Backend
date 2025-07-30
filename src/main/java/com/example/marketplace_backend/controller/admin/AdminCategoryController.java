package com.example.marketplace_backend.controller.admin;

import com.example.marketplace_backend.DTO.Requests.models.CategoryRequest;
import com.example.marketplace_backend.DTO.Responses.models.CategoryResponse;
import com.example.marketplace_backend.Model.Category;
import com.example.marketplace_backend.Service.Impl.CategoryServiceImpl;
import com.example.marketplace_backend.Service.Impl.ConverterService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/categories")
public class AdminCategoryController {
    private final CategoryServiceImpl categoryService;
    private final ConverterService converterService;
    private static final Logger log = LoggerFactory.getLogger(AdminCategoryController.class);

    @GetMapping
    public ResponseEntity<Page<CategoryResponse>> getAllCategories(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(categoryService.findAllActive(pageable).map(converterService::convertToCategoryResponse));
    }

    @GetMapping("/inactive")
    public ResponseEntity<Page<CategoryResponse>> getInactiveCategories(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Category> categories = categoryService.findAllDeActive(pageable);
        Page<CategoryResponse> responses = categories.map(converterService::convertToCategoryResponse);
        return ResponseEntity.ok(responses);
    }


    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponse> getCategoryById(@PathVariable UUID id) {
        Optional<Category> category = categoryService.findById(id);
        return category
                .map(converterService::convertToCategoryResponse)
                .map(ResponseEntity::ok)
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
    public ResponseEntity<CategoryResponse> restoreCategory(@PathVariable UUID id) {
        try {
            categoryService.restore(id);
            Optional<Category> category = categoryService.findById(id);
            return category
                    .map(converterService::convertToCategoryResponse)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
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

    @GetMapping("/priority")
    public ResponseEntity<Page<CategoryResponse>> getCategoryByPriority(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        if (categoryService.findCategoriesByPriority(pageable) == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(categoryService.findCategoriesByPriority(pageable).map(converterService::convertToCategoryResponse));
    }
}