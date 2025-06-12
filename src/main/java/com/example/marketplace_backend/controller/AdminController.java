package com.example.marketplace_backend.controller;

import com.example.marketplace_backend.Model.Category;
import com.example.marketplace_backend.Model.FileEntity;
import com.example.marketplace_backend.Model.Product;
import com.example.marketplace_backend.Model.Subcategory;
import com.example.marketplace_backend.Service.Impl.*;
import com.example.marketplace_backend.controller.Requests.models.CategoryRequest;
import com.example.marketplace_backend.controller.Requests.models.ProductRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;
import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
public class AdminController {
    private final UserServiceImpl userService;
    private final OrderServiceImpl orderService;
    private final ProductServiceImpl productService;
    private final CategoryServiceImpl categoryService;
    private final SubcategoryServiceImpl subcategoryService;
    private final FileUploadService fileUploadService;

    // Products

    @GetMapping("/products/list")
    public ResponseEntity<List<Product>> getAllProducts() {
        List<Product> products = productService.findAll();
        return ResponseEntity.ok(products);
    }

    @PostMapping(value = "/products/edit/{id}", consumes = {"multipart/form-data"})
    public ResponseEntity<Product> editProduct(
            @PathVariable Long id,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Double price,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Long subcategoryId,
            @RequestParam(required = false) MultipartFile image
    ) throws IOException {
        Product product = productService.getById(id);

        if (name != null) product.setName(name);
        if (price != null) product.setPrice(price);
        if (description != null) product.setDescription(description);
        if (categoryId != null) product.setCategory(categoryService.getById(categoryId));
        if (subcategoryId != null) product.setSubcategory(subcategoryService.getById(subcategoryId));

        if (image != null && !image.isEmpty()) {
            FileEntity savedImage = fileUploadService.saveImage(image);
            product.setImage(savedImage);
        }

        Product updatedProduct = productService.save(product);
        return ResponseEntity.ok(updatedProduct);
    }

    @PostMapping("/products/create")
    public ResponseEntity<Product> createProduct(
            @RequestParam String name,
            @RequestParam double price,
            @RequestParam String description,
            @RequestParam Long categoryId,
            @RequestParam Long subcategoryId,
            @RequestParam MultipartFile image) throws Exception {
        ProductRequest dto = new ProductRequest();
        dto.setName(name);
        dto.setPrice(price);
        dto.setDescription(description);
        dto.setCategoryId(categoryId);
        dto.setSubcategoryId(subcategoryId);
        dto.setImage(image);

        return ResponseEntity.ok(productService.createProduct(dto));
    }


    @DeleteMapping("/products/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        Product product = productService.getById(id);
        product.setDeleted(true);
        productService.save(product);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/products/restore/{id}")
    public ResponseEntity<Product> restoreProduct(@PathVariable Long id) {
        Product product = productService.getById(id);
        product.setDeleted(false);
        productService.save(product);
        return ResponseEntity.ok(product);
    }


    // Categories

    @GetMapping("/categories")
    public ResponseEntity<List<Category>> getAllCategories() {
        return ResponseEntity.ok(categoryService.findAllWithProducts());
    }

    @DeleteMapping("/categories/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        Category category = categoryService.getById(id);
        category.setDeleted(true);
        productService.deActiveProductByCategory(category);
        categoryService.save(category);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/categories/restore/{id}")
    public ResponseEntity<Category> restoreCategory(@PathVariable Long id) {
        Category category = categoryService.getById(id);
        category.setDeleted(false);
        categoryService.save(category);
        return ResponseEntity.ok(category);
    }

    @PostMapping("/categories/create")
    public ResponseEntity<Category> createCategoryWithImage(
            @RequestParam String name,
            @RequestParam String description,
            @RequestParam MultipartFile image) throws Exception {

        Category category = new Category();
        category.setName(name);
        category.setDescription(description);
        category.setDeleted(false);
        FileEntity savedImage = fileUploadService.saveImage(image);
        category.setImage(savedImage);

        categoryService.save(category);
        return ResponseEntity.ok(category);
    }

    @PostMapping(value = "/categories/edit/{id}", consumes = {"multipart/form-data"})
    public ResponseEntity<Category> editCategory(
            @PathVariable Long id,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) MultipartFile image
    ) throws IOException {
        Category category = categoryService.getById(id);

        if (name != null && !name.isEmpty()) {
            category.setName(name);
        }
        if (description != null && !description.isEmpty()) {
            category.setDescription(description);
        }
        if (image != null && !image.isEmpty()) {
            FileEntity savedImage = fileUploadService.saveImage(image);
            category.setImage(savedImage);
        }

        categoryService.save(category);
        return ResponseEntity.ok(category);
    }


    // subcategory


    @PostMapping("/subcategories/create")
    public ResponseEntity<Subcategory> createSubcategoryWithImage(
            @RequestParam String name,
            @RequestParam String description,
            @RequestParam Long categoryId,
            @RequestParam(required = false) Long subcategoryId,
            @RequestParam MultipartFile image) throws Exception {

        Subcategory subcategory = new Subcategory();
        subcategory.setName(name);
        subcategory.setDescription(description);

        subcategory.setCategory(categoryService.getById(categoryId));

        if (subcategoryId != null) {
            subcategory.setSubcategory(subcategoryService.getById(subcategoryId));
        }

        FileEntity savedImage = fileUploadService.saveImage(image);
        subcategory.setImage(savedImage);

        subcategoryService.save(subcategory);
        return ResponseEntity.ok(subcategory);
    }

    @PostMapping(value = "/subcategories/edit/{id}", consumes = {"multipart/form-data"})
    public ResponseEntity<Subcategory> editSubcategory(
            @PathVariable Long id,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Long subcategoryId,
            @RequestParam(required = false) MultipartFile image
    ) throws IOException {
        Subcategory subcategory = subcategoryService.getById(id);

        if (name != null && !name.isEmpty()) {
            subcategory.setName(name);
        }
        if (description != null && !description.isEmpty()) {
            subcategory.setDescription(description);
        }
        if (categoryId != null) {
            subcategory.setCategory(categoryService.getById(categoryId));
        }
        if (subcategoryId != null) {
            subcategory.setSubcategory(subcategoryService.getById(subcategoryId));
        }
        if (image != null && !image.isEmpty()) {
            FileEntity savedImage = fileUploadService.saveImage(image);
            subcategory.setImage(savedImage);
        }

        subcategoryService.save(subcategory);
        return ResponseEntity.ok(subcategory);
    }

    @DeleteMapping("/subcategories/{id}")
    public ResponseEntity<Void> deleteSubcategory(@PathVariable Long id) {
        Subcategory subcategory = subcategoryService.getById(id);
        subcategory.setDeleted(true);
        subcategoryService.save(subcategory);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/subcategories/restore/{id}")
    public ResponseEntity<Subcategory> restoreSubcategory(@PathVariable Long id) {
        Subcategory subcategory = subcategoryService.getById(id);
        subcategory.setDeleted(false);
        subcategoryService.save(subcategory);
        return ResponseEntity.ok(subcategory);
    }

}