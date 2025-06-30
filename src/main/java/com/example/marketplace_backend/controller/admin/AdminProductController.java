package com.example.marketplace_backend.controller.admin;

import com.example.marketplace_backend.DTO.Requests.models.ProductRequest;
import com.example.marketplace_backend.Model.Product;
import com.example.marketplace_backend.Repositories.BrandRepository;
import com.example.marketplace_backend.Repositories.SubcategoryRepository;
import com.example.marketplace_backend.Service.Impl.FileUploadService;
import com.example.marketplace_backend.Service.Impl.ProductServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.UUID;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/products")
public class AdminProductController {
    private final ProductServiceImpl productService;
    private final FileUploadService fileUploadService;
    private final BrandRepository brandRepository;
    private final SubcategoryRepository subcategoryRepository;

    @PostMapping(value = "/create", consumes = {"multipart/form-data"})
    public ResponseEntity<Product> createProductWithImages(
            @RequestParam ProductRequest request
    ) throws Exception {
        Product savedProduct = productService.createProduct(request);
        return ResponseEntity.ok(savedProduct);
    }

    @PostMapping(value = "/edit/{id}", consumes = {"multipart/form-data"})
    public ResponseEntity<Product> editProduct(
            @PathVariable UUID id,
            @RequestParam() ProductRequest request
    ) throws Exception {

        return ResponseEntity.ok(productService.editProduct(id, request));
    }


    @DeleteMapping("/soft-delete/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable UUID id) {
        Product product = productService.getById(id);
        product.setDeletedAt(LocalDateTime.now());
        productService.save(product);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/restore/{id}")
    public ResponseEntity<Product> restoreProduct(@PathVariable UUID id) {
        Product product = productService.getById(id);
        product.setDeletedAt(null);
        productService.save(product);
        return ResponseEntity.ok(product);
    }
}
