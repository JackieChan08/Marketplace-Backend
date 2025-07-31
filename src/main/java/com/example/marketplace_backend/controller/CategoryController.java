package com.example.marketplace_backend.controller;

import com.example.marketplace_backend.DTO.Responses.models.CategoryResponse;
import com.example.marketplace_backend.DTO.Responses.models.CategoryWithSubcategoryResponse;
import com.example.marketplace_backend.DTO.Responses.models.FileResponse;
import com.example.marketplace_backend.DTO.Responses.models.ProductResponse;
import com.example.marketplace_backend.Model.Category;
import com.example.marketplace_backend.Model.FileEntity;
import com.example.marketplace_backend.Model.Product;
import com.example.marketplace_backend.Repositories.CategoryRepository;
import com.example.marketplace_backend.Repositories.ProductRepository;
import com.example.marketplace_backend.Service.Impl.CategoryServiceImpl;
import com.example.marketplace_backend.Service.Impl.ConverterService;
import com.example.marketplace_backend.Service.Impl.ProductServiceImpl;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
@Tag(name = "Category Controller", description = "API для управления категориями товаров")
public class CategoryController {

    private final CategoryServiceImpl categoryService;
    private final ConverterService  converter;

    @Value("${app.base-url}")
    private String baseUrl;

    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponse> getById(
            @Parameter(description = "ID категории", required = true, example = "123")
            @PathVariable UUID id
    ) {
        Category category = categoryService.getById(id);
        if (category == null || category.getDeletedAt() != null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        CategoryResponse response = converter.convertToCategoryResponse(category);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<Page<CategoryResponse>> getCategories(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);

        Page<CategoryResponse> responses = categoryService.findAllActive(pageable).map(converter::convertToCategoryResponse);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAll() {
        try {
            List<CategoryResponse> responses = categoryService.getAll().stream()
                    .map(converter::convertToCategoryResponse)
                    .toList();
            return ResponseEntity.ok(responses);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Ошибка при получении категории" + e.getMessage());
        }
    }

    @GetMapping("/with-subcategories/all")
    public ResponseEntity<?> getAllCategoriesWithSubcategories() {
        try {
            List<CategoryWithSubcategoryResponse> responses = categoryService.getAllCategoriesWithSubcategories();
            return ResponseEntity.ok(responses);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Ошибка при получении категорий с подкатегориями: " + e.getMessage());
        }
    }

    @GetMapping("/{id}/with-subcategories")
    public ResponseEntity<CategoryWithSubcategoryResponse> getCategoryWithSubcategoriesById(
            @Parameter(description = "ID категории", required = true, example = "123")
            @PathVariable UUID id
    ) {
        try {
            CategoryWithSubcategoryResponse response = categoryService.getCategoryWithSubcategoriesById(id);
            return ResponseEntity.ok(response);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
