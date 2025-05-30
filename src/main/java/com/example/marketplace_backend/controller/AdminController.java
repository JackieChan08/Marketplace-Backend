package com.example.marketplace_backend.controller;

import com.example.marketplace_backend.Model.Category;
import com.example.marketplace_backend.Model.Product;
import com.example.marketplace_backend.Service.Impl.CategoryServiceImpl;
import com.example.marketplace_backend.Service.Impl.OrderServiceImpl;
import com.example.marketplace_backend.Service.Impl.ProductServiceImpl;
import com.example.marketplace_backend.Service.Impl.UserServiceImpl;
import com.example.marketplace_backend.controller.Requests.models.CategoryRequest;
import com.example.marketplace_backend.controller.Requests.models.ProductRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Base64;
import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController {
    private final UserServiceImpl userService;
    private final OrderServiceImpl orderService;
    private final ProductServiceImpl productService;
    private final CategoryServiceImpl categoryService;

    @GetMapping("/products")
    public ResponseEntity<List<Product>> getAllProducts() {
        List<Product> products = productService.findAll();
        return ResponseEntity.ok(products);
    }


    @PostMapping(value = "/products/edit/{id}", consumes = "application/json")
    public ResponseEntity<Product> editProduct(
            @PathVariable Long id,
            @RequestBody ProductRequest productRequest) throws IOException {

        Product product = productService.getById(id);

        if (productRequest.getName() != null) {
            product.setName(productRequest.getName());
        }

        if (productRequest.getPrice() != 0) {
            product.setPrice(productRequest.getPrice());
        }

        if (productRequest.getDescription() != null) {
            product.setDescription(productRequest.getDescription());
        }

        if (productRequest.getCategoryId() != null) {
            product.setCategory(categoryService.getById(productRequest.getCategoryId()));
        }

        if (productRequest.getImage() != null && !productRequest.getImage().isEmpty()) {
            byte[] imageBytes = productRequest.getImage().getBytes();
            String encodedImage = Base64.getEncoder().encodeToString(imageBytes);
            product.setImage(encodedImage);
        }

        Product updatedProduct = productService.save(product);
        return ResponseEntity.ok(updatedProduct);
    }


    @PostMapping(value = "/products/create", consumes = "application/json")
    public ResponseEntity<Product> createProduct(@RequestBody ProductRequest productRequest) throws IOException {
        Product product = new Product();
        product.setName(productRequest.getName());
        product.setPrice(productRequest.getPrice());
        product.setDescription(productRequest.getDescription());
        product.setCategory(categoryService.findActiveCategoryById(productRequest.getCategoryId()));

        if (productRequest.getImage() != null && !productRequest.getImage().isEmpty()) {
            byte[] imageBytes = productRequest.getImage().getBytes();
            String encodedImage = Base64.getEncoder().encodeToString(imageBytes);
            product.setImage(encodedImage);
        }

        Product savedProduct = productService.save(product);
        return ResponseEntity.ok(savedProduct);
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

    @PostMapping(value = "/categories/create", consumes = "application/json")
    public ResponseEntity<Category> createCategoryJson(@RequestBody CategoryRequest categoryRequest) {
        Category category = new Category();
        category.setName(categoryRequest.getName());
        category.setDescription(categoryRequest.getDescription());
        category.setImage(categoryRequest.getImage());


        if (categoryRequest.getImage() != null && !categoryRequest.getImage().isEmpty()) {
            byte[] imageBytes = categoryRequest.getImage().getBytes();
            String encodedImage = Base64.getEncoder().encodeToString(imageBytes);
            category.setImage(encodedImage);
        }

        categoryService.save(category);
        return ResponseEntity.ok(category);
    }
    @PostMapping(value = "/categories/edit/{id}", consumes = "application/json")
    public ResponseEntity<Category> editCategory(
            @PathVariable Long id,
            @RequestBody CategoryRequest categoryRequest) throws IOException {

        Category category = categoryService.getById(id);

        if (categoryRequest.getName() != null && !categoryRequest.getName().isEmpty()) {
            category.setName(categoryRequest.getName());
        }

        if (categoryRequest.getDescription() != null && !categoryRequest.getDescription().isEmpty()) {
            category.setDescription(categoryRequest.getDescription());
        }

        if (categoryRequest.getImage() != null && !categoryRequest.getImage().isEmpty()) {
            byte[] imageBytes = categoryRequest.getImage().getBytes();
            String encodedImage = Base64.getEncoder().encodeToString(imageBytes);
            category.setImage(encodedImage);
        }

        categoryService.save(category);
        return ResponseEntity.ok(category);
    }
}