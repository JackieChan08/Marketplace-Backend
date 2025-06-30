package com.example.marketplace_backend.controller.admin;

import com.example.marketplace_backend.DTO.Requests.models.ProductRequest;
import com.example.marketplace_backend.Model.*;
import com.example.marketplace_backend.Model.Intermediate_objects.ProductImage;
import com.example.marketplace_backend.Repositories.BrandRepository;
import com.example.marketplace_backend.Repositories.SubcategoryRepository;
import com.example.marketplace_backend.Service.Impl.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
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
            @RequestParam String name,
            @RequestParam BigDecimal price,
            @RequestParam String description,
            @RequestParam UUID subcategoryId,
            @RequestParam UUID brandId,
            @RequestParam("images") List<MultipartFile> images
    ) throws Exception {

        Product product = new Product();
        product.setName(name);
        product.setPrice(price);
        Description desc = new Description();
        desc.setText(description);
        product.setDeletedAt(null);
        product.setCreatedAt(LocalDateTime.now());

        Subcategory subcategory = subcategoryRepository.findById(subcategoryId)
                .orElseThrow(() -> new RuntimeException("Категория не найдена"));
        product.setSubcategory(subcategory);

        Brand brand = brandRepository.findById(brandId)
                .orElseThrow(() -> new RuntimeException("Бренд не найден"));
        product.setBrand(brand);

        product = productService.save(product);

        List<ProductImage> productImages = new ArrayList<>();
        for (MultipartFile imageFile : images) {
            FileEntity savedImage = fileUploadService.saveImage(imageFile);
            ProductImage productImage = ProductImage.builder()
                    .product(product)
                    .image(savedImage)
                    .build();
            productImages.add(productImage);
        }

        product.setProductImages(productImages);

        product = productService.save(product);

        return ResponseEntity.ok(product);
    }

    @PostMapping(value = "/edit/{id}", consumes = {"multipart/form-data"})
    public ResponseEntity<Product> editProduct(
            @PathVariable UUID id,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Double price,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) UUID categoryId,
            @RequestParam(required = false) UUID brandId,
            @RequestParam(name = "images", required = false) List<MultipartFile> images
    ) throws Exception {

        List<Description> descriptions = null;
        if (description != null && !description.isEmpty()) {
            Description desc = new Description();
            desc.setText(description);
            descriptions = List.of(desc);
        }

        ProductRequest dto = ProductRequest.builder()
                .name(name)
                .price(BigDecimal.valueOf(price != null ? price : 0))
                .descriptions(descriptions)
                .categoryId(categoryId)
                .brandId(brandId)
                .images(images)
                .build();

        return ResponseEntity.ok(productService.editProduct(id, dto));
    }


    @DeleteMapping("/{id}")
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
