package com.example.marketplace_backend.controller.admin;

import com.example.marketplace_backend.DTO.Requests.models.ProductFilterRequest;
import com.example.marketplace_backend.DTO.Requests.models.ProductRequest;
import com.example.marketplace_backend.DTO.Responses.models.ProductResponse;
import com.example.marketplace_backend.Model.Intermediate_objects.ProductColorImage;
import com.example.marketplace_backend.Model.Intermediate_objects.ProductImage;
import com.example.marketplace_backend.Model.Intermediate_objects.ProductStatuses;
import com.example.marketplace_backend.Model.Product;
import com.example.marketplace_backend.Service.Impl.ConverterService;
import com.example.marketplace_backend.Service.Impl.FileUploadService;
import com.example.marketplace_backend.Service.Impl.ProductServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/products")
public class AdminProductController {
    private final ProductServiceImpl productService;
    private final FileUploadService fileUploadService;
    private final ConverterService converterService;


    @GetMapping()
    public ResponseEntity<Page<ProductResponse>> getAllProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Product> products = productService.findAllActive(pageable);

        Page<ProductResponse> responses = products.map(converterService::convertToProductResponse);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/inactive")
    public ResponseEntity<Page<ProductResponse>> getInactiveProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Product> products = productService.findAllDeActive(pageable);

        Page<ProductResponse> responses = products.map(converterService::convertToProductResponse);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("{id}")
    public ResponseEntity<ProductResponse> getProductById(@PathVariable UUID id) {
        Optional<Product> product = productService.findById(id);
        return product
                .map(p -> ResponseEntity.ok(converterService.convertToProductResponse(p)))
                .orElseGet(() -> ResponseEntity.notFound().build());
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
    public ResponseEntity<Void> restoreProduct(@PathVariable UUID id) {
        try {
            productService.restore(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping(value = "/create", consumes = {"multipart/form-data"})
    public ResponseEntity<?> createProductWithImages(
            @ModelAttribute ProductRequest request
    ) throws Exception {
        ProductResponse productResponse = converterService.convertToProductResponse(
                productService.createProduct(request)
        );
        return ResponseEntity.ok(productResponse);
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
    public ResponseEntity<ProductResponse> editProduct(
            @PathVariable UUID id,
            @ModelAttribute ProductRequest request
    ) throws Exception {
        ProductResponse productResponse = converterService.convertToProductResponse(productService.editProduct(id, request));
        return ResponseEntity.ok(productResponse);
    }

//    @DeleteMapping("{productId}/images/{imageId}")
//    public ResponseEntity<Void> deleteProductImage(
//            @PathVariable UUID productId,
//            @PathVariable UUID imageId
//    ) {
//        try {
//            boolean deleted = productService.deleteProductImage(productId, imageId);
//
//            if (!deleted) {
//                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
//            }
//
//            return ResponseEntity.noContent().build();
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
//        }
//    }

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
        Page<ProductResponse> responses = products.map(converterService::convertToProductResponse);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/search")
    public ResponseEntity<Page<ProductResponse>> findByNameContaining(
            @RequestParam String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Product> products = productService.findByNameContaining(query, pageable);

        Page<ProductResponse> responses = products.map(converterService::convertToProductResponse);
        return ResponseEntity.ok(responses);
    }

    @PostMapping("/{id}/images")
    public ResponseEntity<ProductImage> addImage(
            @PathVariable UUID id,
            @RequestParam("file") MultipartFile file
    ) throws Exception {
        return ResponseEntity.ok(productService.addProductImage(id, file));
    }

    @DeleteMapping("/images/{imageId}")
    public ResponseEntity<Void> deleteImage(@PathVariable UUID imageId) {
        productService.deleteProductImage(imageId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/color-images")
    public ResponseEntity<ProductColorImage> addColorImage(
            @PathVariable UUID id,
            @RequestParam("file") MultipartFile file
    ) throws Exception {
        return ResponseEntity.ok(productService.addProductColorImage(id, file));
    }

    @DeleteMapping("/color-images/{imageId}")
    public ResponseEntity<Void> deleteColorImage(@PathVariable UUID imageId) {
        productService.deleteProductColorImage(imageId);
        return ResponseEntity.noContent().build();
    }
}