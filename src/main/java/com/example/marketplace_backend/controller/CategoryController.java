package com.example.marketplace_backend.controller;

import com.example.marketplace_backend.Model.*;
import com.example.marketplace_backend.Service.Impl.*;
import com.example.marketplace_backend.Repositories.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Set;

@Tag(name = "Category Controller", description = "API для управления категориями товаров")
@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final ProductServiceImpl productService;
    private final CategoryServiceImpl categoryService;

    @GetMapping("/{id}")
    public ResponseEntity<Category> getById(
            @Parameter(description = "ID категории", required = true, example = "123")
            @PathVariable Long id
    ) {
        Category category = categoryService.getById(id);
        List<Product> products = category.getProducts();

        if (category == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(category);
    }

    @GetMapping("/list")
    public ResponseEntity<List<Category>> getCategories() {
        List<Category> categories = categoryService.findAllActive();
        if (categories.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(categories);
    }

    @GetMapping("/{id}/image")
    public ResponseEntity<String> getCategoryImage(
            @Parameter(description = "ID категории", required = true, example = "123")
            @PathVariable Long id
    ) {
        Category category = categoryService.getById(id);
        if (category != null && category.getImage() != null) {
            return ResponseEntity.ok(category.getImage());
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
}
