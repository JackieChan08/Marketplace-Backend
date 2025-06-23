package com.example.marketplace_backend.controller;

import com.example.marketplace_backend.Model.FileEntity;
import com.example.marketplace_backend.Model.Product;
import com.example.marketplace_backend.Model.User;
import com.example.marketplace_backend.Repositories.ProductRepository;
import com.example.marketplace_backend.Repositories.UserRepository;
import com.example.marketplace_backend.Service.Impl.CategoryServiceImpl;
import com.example.marketplace_backend.Service.Impl.ProductServiceImpl;
import com.example.marketplace_backend.Service.Impl.UserServiceImpl;
import com.example.marketplace_backend.controller.Responses.FileResponse;
import com.example.marketplace_backend.controller.Responses.ProductResponse;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "Product Controller", description = "API для управления продуктами")
@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductServiceImpl productService;
    private final CategoryServiceImpl categoryService;
    private final UserServiceImpl userService;
    private final ProductRepository productRepository;
    @Value("${app.base-url}")
    private String baseUrl;

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getById(@PathVariable UUID id) {
        Product product = productService.getById(id);
        if (product == null || product.getDeletedAt() != null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        ProductResponse productResponse = convertToProductResponse(product);

        return ResponseEntity.ok(productResponse);
    }

    @GetMapping("/list")
    public ResponseEntity<List<ProductResponse>> getProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = (Pageable) PageRequest.of(page, size);
        Page<Product> products = productRepository.findAll((org.springframework.data.domain.Pageable) pageable);

        List<ProductResponse> responses = products.stream()
                .map(this::convertToProductResponse)
                .toList();

        return ResponseEntity.ok(responses);
    }



//    @GetMapping("/list")
//    public ResponseEntity<List<ProductResponse>> getAllProducts() {
//        List<Product> products = productService.findAllActive();
//        if (products.isEmpty()) {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
//        }
//
//        List<ProductResponse> responses = products.stream()
//                .map(this::convertToProductResponse)
//                .toList();
//
//        return ResponseEntity.ok(responses);
//    }

    @GetMapping("/list/search")
    public ResponseEntity<List<ProductResponse>> findByNameContaining(
            @Parameter(description = "Поисковый запрос", required = true, example = "Пицца")
            @RequestParam String query
    ) {
        List<Product> products = productService.findByNameContaining(query);
        if (products.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        List<ProductResponse> responses = products.stream()
                .map(this::convertToProductResponse)
                .toList();

        return ResponseEntity.ok(responses);
    }

    private ProductResponse convertToProductResponse(Product product) {
        ProductResponse response = new ProductResponse();
        response.setId(product.getId());
        response.setName(product.getName());
        response.setDescriptions(product.getDescriptions());
        response.setCategoryId(product.getCategory().getId());
        response.setCategoryName(product.getCategory().getName());
        response.setBrandId(product.getBrand().getId());

        if (product.getProductImages() != null && !product.getProductImages().isEmpty()) {
            List<FileResponse> images = product.getProductImages().stream()
                    .map(productImage -> {
                        FileEntity image = productImage.getImage(); // ✅ получаем FileEntity из ProductImage
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
