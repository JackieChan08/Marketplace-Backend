package com.example.marketplace_backend.controller;

import com.example.marketplace_backend.Model.Product;
import com.example.marketplace_backend.Repositories.UserRepository;
import com.example.marketplace_backend.Service.Impl.CategoryServiceImpl;
import com.example.marketplace_backend.Service.Impl.ProductServiceImpl;
import com.example.marketplace_backend.Service.Impl.UserServiceImpl;
import com.example.marketplace_backend.controller.Responses.FileResponse;
import com.example.marketplace_backend.controller.Responses.ProductResponse;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Product Controller", description = "API для управления продуктами")
@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductServiceImpl productService;
    private final CategoryServiceImpl categoryService;
    private final UserServiceImpl userService;

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getById(@PathVariable Long id) {
        Product product = productService.getById(id);
        if (product == null || product.isDeleted()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        ProductResponse productResponse = convertToProductResponse(product);

        return ResponseEntity.ok(productResponse);
    }



    @GetMapping("/list")
    public ResponseEntity<List<ProductResponse>> getAllProducts() {
        List<Product> products = productService.findAllActive();
        if (products.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        List<ProductResponse> responses = products.stream()
                .map(this::convertToProductResponse)
                .toList();

        return ResponseEntity.ok(responses);
    }

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
        response.setDescription(product.getDescription());
        response.setCategoryId(product.getCategory().getId());
        response.setCategoryName(product.getCategory().getName());

        if (product.getImage() != null) {
            var image = product.getImage();

            FileResponse fileResponse = new FileResponse();
            fileResponse.setUniqueName(image.getUniqueName());
            fileResponse.setOriginalName(image.getOriginalName());
            fileResponse.setUrl("http://localhost:8080/uploads/" + image.getUniqueName());
            fileResponse.setFileType(image.getFileType());

            response.setImageFile(fileResponse);
        }

        return response;
    }



}
