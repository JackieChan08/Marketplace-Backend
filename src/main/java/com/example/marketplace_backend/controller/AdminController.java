package com.example.marketplace_backend.controller;

import com.example.marketplace_backend.Model.*;
import com.example.marketplace_backend.Model.Intermediate_objects.CategoryImage;
import com.example.marketplace_backend.Model.Intermediate_objects.ProductImage;
import com.example.marketplace_backend.Repositories.BrandRepository;
import com.example.marketplace_backend.Repositories.CategoryRepository;
import com.example.marketplace_backend.Service.Impl.*;
import com.example.marketplace_backend.controller.Requests.models.ProductRequest;
import com.example.marketplace_backend.controller.Requests.models.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;
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

    // Products
    @GetMapping("/products/list")
    public ResponseEntity<List<Product>> getAllProducts() {
        List<Product> products = productService.findAll();
        return ResponseEntity.ok(products);
    }

    @PostMapping(value = "/products/create", consumes = {"multipart/form-data"})
    public ResponseEntity<Product> createProductWithImages(
            @RequestParam String name,
            @RequestParam BigDecimal price,
            @RequestParam("descriprions") List<Description> descriptions,
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
            @RequestParam("descriprions") List<Description> descriptions,
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


//
//    // subcategory
//
//
//    @PostMapping("/subcategories/create")
//    public ResponseEntity<Subcategory> createSubcategoryWithImage(
//            @RequestParam String name,
//            @RequestParam String description,
//            @RequestParam Long categoryId,
//            @RequestParam(required = false) Long subcategoryId) throws Exception {
//
//        Subcategory subcategory = new Subcategory();
//        subcategory.setName(name);
//        subcategory.setDescription(description);
//
//        subcategory.setCategory(categoryService.getById(categoryId));
//
//        if (subcategoryId != null) {
//            subcategory.setSubcategory(subcategoryService.getById(subcategoryId));
//        }
//
//        subcategoryService.save(subcategory);
//        return ResponseEntity.ok(subcategory);
//    }
//
//    @PostMapping(value = "/subcategories/edit/{id}", consumes = {"multipart/form-data"})
//    public ResponseEntity<Subcategory> editSubcategory(
//            @PathVariable Long id,
//            @RequestParam(required = false) String name,
//            @RequestParam(required = false) String description,
//            @RequestParam(required = false) Long categoryId,
//            @RequestParam(required = false) Long subcategoryId) throws IOException {
//        Subcategory subcategory = subcategoryService.getById(id);
//
//        if (name != null && !name.isEmpty()) {
//            subcategory.setName(name);
//        }
//        if (description != null && !description.isEmpty()) {
//            subcategory.setDescription(description);
//        }
//        if (categoryId != null) {
//            subcategory.setCategory(categoryService.getById(categoryId));
//        }
//        if (subcategoryId != null) {
//            subcategory.setSubcategory(subcategoryService.getById(subcategoryId));
//        }
//
//
//        subcategoryService.save(subcategory);
//        return ResponseEntity.ok(subcategory);
//    }
//
//    @DeleteMapping("/subcategories/{id}")
//    public ResponseEntity<Void> deleteSubcategory(@PathVariable Long id) {
//        Subcategory subcategory = subcategoryService.getById(id);
//        subcategory.setDeleted(true);
//        subcategoryService.save(subcategory);
//        return ResponseEntity.noContent().build();
//    }
//
//    @PostMapping("/subcategories/restore/{id}")
//    public ResponseEntity<Subcategory> restoreSubcategory(@PathVariable Long id) {
//        Subcategory subcategory = subcategoryService.getById(id);
//        subcategory.setDeleted(false);
//        subcategoryService.save(subcategory);
//        return ResponseEntity.ok(subcategory);
//    }

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
    @GetMapping("/users/list")
    public List<UserResponse> getAllUsers() {
        return userService.getAll().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    private UserResponse convertToResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getName(),
                user.getRole()
        );
    }

}