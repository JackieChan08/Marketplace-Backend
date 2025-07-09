package com.example.marketplace_backend.controller;

import com.example.marketplace_backend.DTO.Responses.models.SubcategoryResponse;
import com.example.marketplace_backend.Model.Category;
import com.example.marketplace_backend.Model.Subcategory;
import com.example.marketplace_backend.Service.Impl.CategoryServiceImpl;
import com.example.marketplace_backend.Service.Impl.ConverterService;
import com.example.marketplace_backend.Service.Impl.SubcategoryServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/subcategories")
public class SubcategoryController {

    private final SubcategoryServiceImpl subcategoryService;
    private final CategoryServiceImpl categoryService;
    private final ConverterService converter;

    @GetMapping("/{id}")
    public ResponseEntity<SubcategoryResponse> getSubcategoryById(@PathVariable UUID id) {
        Optional<Subcategory> subcategory = subcategoryService.findById(id);
        if (subcategory.isEmpty() || subcategory.get().getDeletedAt() != null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        SubcategoryResponse subcategoryResponse = converter.convertToSubcategoryResponse(subcategory.get());
        return ResponseEntity.ok(subcategoryResponse);
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<Page<SubcategoryResponse>> getSubcategoriesByCategory(
            @PathVariable UUID categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Optional<Category> categoryOpt = categoryService.findById(categoryId);
        if (categoryOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        Pageable pageable = PageRequest.of(page, size);
        Page<Subcategory> subcategories = subcategoryService.findByCategoryActive(categoryOpt.get(), pageable);
        Page<SubcategoryResponse> responses = subcategories.map(converter::convertToSubcategoryResponse);

        return ResponseEntity.ok(responses);
    }

    @GetMapping("/list")
    public ResponseEntity<Page<SubcategoryResponse>> getAllSubcategoriesPaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Subcategory> subcategories = subcategoryService.findAllActive(pageable);
        Page<SubcategoryResponse> responses = subcategories.map(converter::convertToSubcategoryResponse);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/list/search")
    public ResponseEntity<Page<SubcategoryResponse>> searchSubcategoriesByName(
            @RequestParam String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Subcategory> subcategories = subcategoryService.searchByName(query, pageable);
        Page<SubcategoryResponse> responses = subcategories.map(converter::convertToSubcategoryResponse);
        return ResponseEntity.ok(responses);
    }
}
