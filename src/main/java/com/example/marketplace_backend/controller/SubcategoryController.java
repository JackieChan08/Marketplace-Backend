package com.example.marketplace_backend.controller;

import com.example.marketplace_backend.Model.Category;
import com.example.marketplace_backend.Model.Subcategory;
import com.example.marketplace_backend.Service.Impl.CategoryServiceImpl;
import com.example.marketplace_backend.Service.Impl.SubcategoryServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/subcategories")
public class SubcategoryController {
    private final SubcategoryServiceImpl  subcategoryService;
    private final CategoryServiceImpl categoryService;

    @GetMapping()
    public ResponseEntity<List<Subcategory>> getAllSubcategories() {
        return ResponseEntity.ok(subcategoryService.findAllActive());
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
}
