package com.example.marketplace_backend.controller;

import com.example.marketplace_backend.Model.*;
import com.example.marketplace_backend.Model.Intermediate_objects.BrandImage;
import com.example.marketplace_backend.Model.Intermediate_objects.CategoryImage;
import com.example.marketplace_backend.Model.Intermediate_objects.ProductImage;
import com.example.marketplace_backend.Repositories.BrandRepository;
import com.example.marketplace_backend.Repositories.CategoryRepository;
import com.example.marketplace_backend.Service.Impl.*;
import com.example.marketplace_backend.controller.Requests.models.ProductRequest;
import com.example.marketplace_backend.controller.Requests.models.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
public class AdminController {
    private final UserServiceImpl userService;
    private final OrderServiceImpl orderService;
    private final ProductServiceImpl productService;
    private final CategoryServiceImpl categoryService;
    private final BrandServiceImpl brandService;
    private final FileUploadService fileUploadService;
    private final BrandRepository brandRepository;
    private final CategoryRepository categoryRepository;
    private final SubcategoryServiceImpl subcategoryServiceImpl;

    // Products
//    @GetMapping("/products/list")
//    public ResponseEntity<List<Product>> getAllProducts() {
//        List<Product> products = productService.findAll();
//        return ResponseEntity.ok(products);
//    }

    @PostMapping(value = "/products/create", consumes = {"multipart/form-data"})
    public ResponseEntity<Product> createProductWithImages(
            @RequestParam String name,
            @RequestParam BigDecimal price,
            @RequestParam("descriptions") List<Description> descriptions,
            @RequestParam UUID categoryId,
            @RequestParam UUID brandId,
            @RequestParam("images") List<MultipartFile> images
    ) throws Exception {

        Product product = new Product();
        product.setName(name);
        product.setPrice(price);
        product.setDescriptions(descriptions);
        product.setDeletedAt(null);
        product.setCreatedAt(LocalDateTime.now());

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Категория не найдена"));
        product.setCategory(category);

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

    @PostMapping(value = "/products/edit/{id}", consumes = {"multipart/form-data"})
    public ResponseEntity<Product> editProduct(
            @PathVariable UUID id,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Double price,
            @RequestParam("descriptions") List<Description> descriptions,
            @RequestParam(required = false) UUID categoryId,
            @RequestParam(required = false) UUID brandId,
            @RequestParam(name = "images", required = false) List<MultipartFile> images
    ) throws Exception {
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


    @DeleteMapping("/products/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable UUID id) {
        Product product = productService.getById(id);
        product.setDeletedAt(LocalDateTime.now());
        productService.save(product);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/products/restore/{id}")
    public ResponseEntity<Product> restoreProduct(@PathVariable UUID id) {
        Product product = productService.getById(id);
        product.setDeletedAt(null);
        productService.save(product);
        return ResponseEntity.ok(product);
    }


    // Categories

    @GetMapping("/categories")
    public ResponseEntity<List<Category>> getAllCategories() {
        return ResponseEntity.ok(categoryService.findAllWithProducts());
    }

    @DeleteMapping("/categories/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable UUID id) {
        Category category = categoryService.getById(id);
        category.setDeletedAt(LocalDateTime.now());
        productService.deActiveProductByCategory(category);
        categoryService.save(category);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/categories/restore/{id}")
    public ResponseEntity<Category> restoreCategory(@PathVariable UUID id) {
        Category category = categoryService.getById(id);
        category.setDeletedAt(null);
        categoryService.save(category);
        return ResponseEntity.ok(category);
    }

    @PostMapping(value = "/categories/create", consumes = {"multipart/form-data"})
    public ResponseEntity<Category> createCategoryWithImages(
            @RequestParam String name,
            @RequestParam("images") List<MultipartFile> images
    ) throws Exception {

        Category category = new Category();
        category.setName(name);
        category.setDeletedAt(null);
        category.setCreatedAt(LocalDateTime.now());

        category = categoryService.save(category);

        List<CategoryImage> categoryImages = new ArrayList<>();
        for (MultipartFile image : images) {
            FileEntity savedImage = fileUploadService.saveImage(image);
            CategoryImage categoryImage = CategoryImage.builder()
                    .category(category)
                    .image(savedImage)
                    .build();
            categoryImages.add(categoryImage);
        }

        category.setCategoryImages(categoryImages);

        category = categoryService.save(category);

        return ResponseEntity.ok(category);
    }



    @PostMapping(value = "/categories/edit/{id}", consumes = {"multipart/form-data"})
    public ResponseEntity<Category> editCategory(
            @PathVariable UUID id,
            @RequestParam(required = false) String name,
            @RequestParam(name = "images", required = false) List<MultipartFile> images
    ) throws IOException {
        Category category = categoryService.getById(id);

        if (name != null && !name.isEmpty()) {
            category.setName(name);
        }

        if (images != null && !images.isEmpty()) {
            List<CategoryImage> newCategoryImages = images.stream()
                    .map(image -> {
                        try {
                            FileEntity savedImage = fileUploadService.saveImage(image);
                            return CategoryImage.builder()
                                    .category(category)
                                    .image(savedImage)
                                    .build();
                        } catch (IOException e) {
                            throw new RuntimeException("Ошибка при сохранении изображения", e);
                        }
                    })
                    .toList();

            if (category.getCategoryImages() != null) {
                category.getCategoryImages().addAll(newCategoryImages);
            } else {
                category.setCategoryImages(newCategoryImages);
            }
        }

        categoryService.save(category);
        return ResponseEntity.ok(category);
    }

    // subcategory
    @PostMapping("/subcategories/create")
    public ResponseEntity<Subcategory> createSubcategory(
            @RequestParam String name,
            @RequestParam UUID categoryId) {

        Subcategory subcategory = Subcategory.builder()
                .name(name)
                .category(categoryService.getById(categoryId))
                .build();

        Subcategory saved = subcategoryServiceImpl.save(subcategory);
        return ResponseEntity.ok(saved);
    }

    @PostMapping(value = "/subcategories/edit/{id}", consumes = {"multipart/form-data"})
    public ResponseEntity<Subcategory> editSubcategory(
            @PathVariable UUID id,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) UUID categoryId) {

        Subcategory subcategory = subcategoryServiceImpl.getById(id);

        if (name != null && !name.isEmpty()) {
            subcategory.setName(name);
        }

        if (categoryId != null) {
            subcategory.setCategory(categoryService.getById(categoryId));
        }

        Subcategory updated = subcategoryServiceImpl.save(subcategory);
        return ResponseEntity.ok(updated);
    }


    @DeleteMapping("/subcategories/{id}")
    public ResponseEntity<Void> deleteSubcategory(@PathVariable UUID id) {
        Subcategory subcategory = subcategoryServiceImpl.getById(id);
        subcategoryServiceImpl.delete(subcategory);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/subcategories/restore/{id}")
    public ResponseEntity<Subcategory> restoreSubcategory(@PathVariable UUID id) {
        Subcategory subcategory = subcategoryServiceImpl.getDeletedById(id);
        subcategory.setDeletedAt(null);
        Subcategory restored = subcategoryServiceImpl.save(subcategory);
        return ResponseEntity.ok(restored);
    }


    // Orders
    @GetMapping("/orders")
    public ResponseEntity<List<Order>> getWholesaleOrders() {
        return ResponseEntity.ok(orderService.getAllOrders());
    }

    @PutMapping("/orders/{orderId}/status")
    public ResponseEntity<Order> updateStatus(@PathVariable UUID orderId,
                                              @RequestParam String status) {
        return ResponseEntity.ok(orderService.updateOrderStatus(orderId, status));
    }

    @PutMapping("/orders/{orderId}/comment")
    public ResponseEntity<Order> updateComment(@PathVariable UUID orderId,
                                               @RequestParam String comment) {
        return ResponseEntity.ok(orderService.updateOrderComment(orderId, comment));
    }
    @PutMapping("/orders/{orderId}/address")
    public ResponseEntity<Order> updateAddress(@PathVariable UUID orderId,
                                               @RequestParam String address) {
        return ResponseEntity.ok(orderService.updateOrderAddress(orderId, address));
    }

    //User

    @GetMapping("/users/search")
    public ResponseEntity<Page<UserResponse>> searchUsers(
            @RequestParam String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(userService.searchUsers(query, pageable));
    }

    //Brand
    @GetMapping("/brands")
    public ResponseEntity<List<Brand>> getAllBrands() {
        return ResponseEntity.ok(brandService.findAllActive());
    }

    @DeleteMapping("brands/hard-delete/{id}")
    public ResponseEntity<Void> hardDeleteBrand(@PathVariable UUID id) {
        Brand brand = brandService.getById(id);
        brandRepository.delete(brand);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/brands/{id}")
    public ResponseEntity<Void> softDeleteBrand(@PathVariable UUID id) {
        Brand brand = brandService.getById(id);
        brand.setDeletedAt(LocalDateTime.now());
        productService.deActiveProductByBrand(brand);
        brandService.save(brand);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("brands/restore/{id}")
    public ResponseEntity<Void> restoreBrand(@PathVariable UUID id) {
        Brand brand = brandService.getById(id);
        brand.setDeletedAt(null);
        brandService.save(brand);
        return ResponseEntity.noContent().build();
    }

    @PostMapping(value = "/brands/create", consumes = {"multipart/form-data"})
    public ResponseEntity<Brand> createBrandWithImages(
            @RequestParam String name,
            @RequestParam("images") List<MultipartFile> images
    ) throws Exception {
        Brand brand = new Brand();
        brand.setName(name);
        brand.setDeletedAt(null);
        brand.setCreatedAt(LocalDateTime.now());

        brand = brandService.save(brand);

        List<BrandImage> brandImages = new ArrayList<>();
        for(MultipartFile image: images) {
            FileEntity savedImage = fileUploadService.saveImage(image);
            BrandImage brandImage = BrandImage.builder()
                    .brand(brand)
                    .image(savedImage)
                    .build();
            brandImages.add(brandImage);
        }

        brand.setBrandImages(brandImages);
        brand = brandService.save(brand);
        return ResponseEntity.ok(brand);
    }

    @PostMapping(value = "brands/edit/{id}", consumes = {"multipart/form-data"})
    public ResponseEntity<Brand> editBrand (
            @PathVariable UUID id,
            @RequestParam(required = false) String name,
            @RequestParam(name = "images", required = false) List<MultipartFile> images
    ) throws Exception {
        Brand brand = brandService.getById(id);

        if (name != null && !name.isEmpty()) {
            brand.setName(name);
        }

        if (images != null && !images.isEmpty()) {
            List<BrandImage> newBrandImages = images.stream()
                    .map(image -> {
                        try {
                            FileEntity savedImage = fileUploadService.saveImage(image);
                            return BrandImage.builder()
                                    .brand(brand)
                                    .image(savedImage)
                                    .build();
                        } catch (IOException e) {
                            throw new RuntimeException("Ошибка при сохранении изображения", e);
                        }
                    })
                    .toList();

            if (brand.getBrandImages() != null) {
                brand.getBrandImages().addAll(newBrandImages);
            } else {
                brand.setBrandImages(newBrandImages);
            }
        }

        brandService.save(brand);
        return ResponseEntity.ok(brand);
    }


}