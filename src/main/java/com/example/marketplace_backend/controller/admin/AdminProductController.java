package com.example.marketplace_backend.controller.admin;

import com.example.marketplace_backend.DTO.Requests.models.ProductRequest;
import com.example.marketplace_backend.Model.Category;
import com.example.marketplace_backend.Model.Product;
import com.example.marketplace_backend.Repositories.BrandRepository;
import com.example.marketplace_backend.Repositories.SubcategoryRepository;
import com.example.marketplace_backend.Service.Impl.FileUploadService;
import com.example.marketplace_backend.Service.Impl.ProductServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/products")
public class AdminProductController {
    private final ProductServiceImpl productService;
    private final FileUploadService fileUploadService;
    private final BrandRepository brandRepository;
    private final SubcategoryRepository subcategoryRepository;

    @GetMapping()
    public ResponseEntity<List<Product>> getAllProducts() {
        return ResponseEntity.ok(productService.findAllActive());
    }

    @GetMapping("/inactive")
    public ResponseEntity<List<Product>> getInactiveProducts() {
        return ResponseEntity.ok(productService.findAllDeActive());
    }

    @GetMapping("{id}")
    public ResponseEntity<Product> getProductById(@PathVariable UUID id) {
        Optional<Product> product = productService.findById(id);
        return product.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/exist/{name}")
    public ResponseEntity<Boolean> productExistByName(@PathVariable String name) {
        return ResponseEntity.ok(productService.existsByName(name));
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Long>> getProductStats() {
        Map<String, Long> stats = new HashMap<>();
        stats.put("activeProducts", productService.countActiveProducts());
        stats.put("inactiveProducts", productService.countDeActiveProducts());
        return ResponseEntity.ok(stats);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> softDeleteProduct(@PathVariable UUID id) {
        try {
            productService.softDelete(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/restore/{id}")
    public ResponseEntity<Product> restoreProduct(@PathVariable UUID id) {
        try {
            productService.restore(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping(value = "/create", consumes = {"multipart/form-data"})
    public ResponseEntity<Product> createProductWithImages(
            @ModelAttribute ProductRequest request
    ) throws Exception {
        return ResponseEntity.ok(productService.createProduct(request));
    }

    @DeleteMapping("{id}/permanent")
    public ResponseEntity<Void> permanentDeleteProduct(@PathVariable UUID id) {
        try {
            productService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/purge")
    public ResponseEntity<Void> purgeProducts() {
        try {
            productService.purgeDeletedProducts();
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping(value = "/edit/{id}", consumes = {"multipart/form-data"})
    public ResponseEntity<Product> editProduct(
            @PathVariable UUID id,
            @ModelAttribute ProductRequest request
    ) throws Exception {
        return ResponseEntity.ok(productService.editProduct(id, request));
    }

    @DeleteMapping("{productId}/images/{imageId}")
    public ResponseEntity<Void> deleteProductImage(
            @PathVariable UUID productId,
            @PathVariable UUID imageId
    ) {
        try {
            boolean deleted = productService.deleteProductImage(productId, imageId);

            if (!deleted) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }

            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }









}
