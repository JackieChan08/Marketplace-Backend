package com.example.marketplace_backend.controller;

import com.example.marketplace_backend.Model.Category;
import com.example.marketplace_backend.Model.Product;
import com.example.marketplace_backend.Service.Impl.CategoryServiceImpl;
import com.example.marketplace_backend.Service.Impl.ProductServiceImpl;
import com.example.marketplace_backend.controller.Responses.*;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
@Tag(name = "Category Controller", description = "API для управления категориями товаров")
public class CategoryController {

    private final ProductServiceImpl productService;
    private final CategoryServiceImpl categoryService;

    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponse> getById(
            @Parameter(description = "ID категории", required = true, example = "123")
            @PathVariable Long id
    ) {
        Category category = categoryService.getById(id);
        if (category == null || category.isDeleted()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        CategoryResponse response = convertToCategoryResponse(category);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/list")
    public ResponseEntity<List<CategoryResponse>> getCategories() {
        List<Category> categories = categoryService.findAllActive();
        if (categories.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        List<CategoryResponse> responses = categories.stream()
                .map(this::convertToCategoryResponse)
                .toList();

        return ResponseEntity.ok(responses);
    }

    private CategoryResponse convertToCategoryResponse(Category category) {
        CategoryResponse response = new CategoryResponse();
        response.setId(category.getId());
        response.setName(category.getName());
        response.setDescription(category.getDescription());

        List<ProductResponse> productResponses = category.getProducts().stream()
                .filter(product -> !product.isDeleted())
                .map(this::convertToProductResponse)
                .toList();

        response.setProducts(productResponses);

        return response;
    }

    private ProductResponse convertToProductResponse(Product product) {
        ProductResponse response = new ProductResponse();
        response.setId(product.getId());
        response.setName(product.getName());
        response.setDescription(product.getDescription());
        response.setCategoryId(product.getCategory().getId());
        response.setCategoryName(product.getCategory().getName());

        if (product.getImage() != null) {
            FileResponse fileResponse = new FileResponse();
            fileResponse.setUniqueName(product.getImage().getUniqueName());
            fileResponse.setOriginalName(product.getImage().getOriginalName());
            fileResponse.setUrl("http://localhost:8080/uploads/" + product.getImage().getUniqueName());
            fileResponse.setFileType(product.getImage().getFileType());
            response.setImageFile(fileResponse);
        }

        return response;
    }
}
