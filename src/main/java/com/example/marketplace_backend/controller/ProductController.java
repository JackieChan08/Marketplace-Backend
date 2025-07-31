package com.example.marketplace_backend.controller;

import com.example.marketplace_backend.DTO.Requests.models.ProductFilterRequest;
import com.example.marketplace_backend.DTO.Responses.models.ProductResponse;
import com.example.marketplace_backend.Model.Product;
import com.example.marketplace_backend.Service.Impl.ConverterService;
import com.example.marketplace_backend.Service.Impl.ProductServiceImpl;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(name = "Product Controller", description = "API для управления продуктами")
@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductServiceImpl productService;
    private final ConverterService  converter;

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getById(@PathVariable UUID id) {
        Product product = productService.getById(id);
        if (product == null || product.getDeletedAt() != null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        ProductResponse productResponse = converter.convertToProductResponse(product);

        return ResponseEntity.ok(productResponse);
    }

    @GetMapping
    public ResponseEntity<Page<ProductResponse>> getProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Product> products = productService.findAllActive(pageable);

        Page<ProductResponse> responses = products.map(converter::convertToProductResponse);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/search")
    public ResponseEntity<Page<ProductResponse>> findByNameContaining(
            @RequestParam String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Product> products = productService.findByNameContainingActive(query, pageable);

        Page<ProductResponse> responses = products.map(converter::convertToProductResponse);
        return ResponseEntity.ok(responses);
    }
    @GetMapping("/brand")
    public ResponseEntity<Page<ProductResponse>> getProductsByBrand(
            @RequestParam UUID brandId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Product> products = productService.findAllActiveByBrand(brandId, pageable);
        Page<ProductResponse> responses = products.map(converter::convertToProductResponse);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/subcategory")
    public ResponseEntity<Page<ProductResponse>> getProductsBySubcategory(
            @RequestParam UUID subcategoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Product> products = productService.findAllActiveBySubcategory(subcategoryId, pageable);
        Page<ProductResponse> responses = products.map(converter::convertToProductResponse);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/category")
    public ResponseEntity<Page<ProductResponse>> getProductsByCategory(
            @RequestParam UUID categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Product> products = productService.findAllActiveByCategoryId(categoryId, pageable);
        Page<ProductResponse> responses = products.map(converter::convertToProductResponse);
        return ResponseEntity.ok(responses);
    }

    @PostMapping("/filter")
    public ResponseEntity<Page<ProductResponse>> filterProducts(@RequestBody ProductFilterRequest filterRequest) {
        return ResponseEntity.ok(productService.filterProducts(filterRequest));
    }

    @GetMapping("/status")
    public ResponseEntity<Page<ProductResponse>> getAllProductsByStatus(
            @RequestParam UUID statusId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Product> products = productService.findAllByStatus(statusId, pageable);
        Page<ProductResponse> responses = products.map(converter::convertToProductResponse);
        return ResponseEntity.ok(responses);
    }
}
