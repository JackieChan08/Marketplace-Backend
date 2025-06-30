package com.example.marketplace_backend.controller;

import com.example.marketplace_backend.DTO.Responses.CategoryResponse;
import com.example.marketplace_backend.DTO.Responses.FileResponse;
import com.example.marketplace_backend.DTO.Responses.ProductResponse;
import com.example.marketplace_backend.Model.Category;
import com.example.marketplace_backend.Model.FileEntity;
import com.example.marketplace_backend.Model.Product;
import com.example.marketplace_backend.Repositories.CategoryRepository;
import com.example.marketplace_backend.Service.Impl.CategoryServiceImpl;
import com.example.marketplace_backend.Service.Impl.ProductServiceImpl;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
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

    private final ProductServiceImpl productService;
    private final CategoryServiceImpl categoryService;
    private final CategoryRepository categoryRepository;
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

        CategoryResponse response = convertToCategoryResponse(category);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/list")
    public ResponseEntity<Page<CategoryResponse>> getCategories(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Category> categories = categoryService.findAll(pageable);

        Page<CategoryResponse> responses = categories.map(this::convertToCategoryResponse);
        return ResponseEntity.ok(responses);
    }


    private CategoryResponse convertToCategoryResponse(Category category) {
        CategoryResponse response = new CategoryResponse();
        response.setId(category.getId());
        response.setName(category.getName());

        if (category.getCategoryImages() != null && !category.getCategoryImages().isEmpty()) {
            List<FileResponse> imageFiles = category.getCategoryImages().stream()
                    .map(categoryImage -> {
                        FileEntity image = categoryImage.getImage();
                        FileResponse fileResponse = new FileResponse();
                        fileResponse.setUniqueName(image.getUniqueName());
                        fileResponse.setOriginalName(image.getOriginalName());
                        fileResponse.setUrl(baseUrl + "/uploads/" + image.getUniqueName());
                        fileResponse.setFileType(image.getFileType());
                        return fileResponse;
                    })
                    .toList();

            response.setImageFiles(imageFiles);
        }

        // Получение всех продуктов через подкатегории этой категории
        List<ProductResponse> productResponses = category.getSubcategories().stream() // ← исправлено имя метода
                .filter(subcategory -> subcategory.getDeletedAt() == null) // пропустить удалённые подкатегории
                .flatMap(subcategory -> subcategory.getProducts().stream())
                .filter(product -> product.getDeletedAt() == null)
                .map(this::convertToProductResponse)
                .toList();

        response.setProducts(productResponses);

        return response;
    }

    private ProductResponse convertToProductResponse(Product product) {
        ProductResponse response = new ProductResponse();
        response.setId(product.getId());
        response.setName(product.getName());
        response.setDescriptions(product.getDescriptions());
        response.setBrandId(product.getBrand().getId());

        if (product.getProductImages() != null && !product.getProductImages().isEmpty()) {
            List<FileResponse> images = product.getProductImages().stream()
                    .map(productImage -> {
                        FileEntity image = productImage.getImage();
                        FileResponse fileResponse = new FileResponse();
                        fileResponse.setUniqueName(image.getUniqueName());
                        fileResponse.setOriginalName(image.getOriginalName());
                        fileResponse.setUrl(baseUrl + "/uploads/" + image.getUniqueName());
                        fileResponse.setFileType(image.getFileType());
                        return fileResponse;
                    })
                    .toList();

            response.setImages(images);
        }

        return response;
    }

}
