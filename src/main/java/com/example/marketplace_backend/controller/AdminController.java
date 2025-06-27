package com.example.marketplace_backend.controller;

import com.example.marketplace_backend.Model.*;
import com.example.marketplace_backend.Model.Intermediate_objects.BrandImage;
import com.example.marketplace_backend.Model.Intermediate_objects.CategoryImage;
import com.example.marketplace_backend.Model.Intermediate_objects.ProductImage;
import com.example.marketplace_backend.Model.Intermediate_objects.SubcategoryImage;
import com.example.marketplace_backend.Repositories.BrandRepository;
import com.example.marketplace_backend.Repositories.SubcategoryRepository;
import com.example.marketplace_backend.Service.Impl.*;
import com.example.marketplace_backend.controller.Requests.models.ProductRequest;
import com.example.marketplace_backend.controller.Responses.UserResponse;
import com.example.marketplace_backend.Model.ProductParameters;
import com.example.marketplace_backend.Model.ProductSubParameters;
import com.example.marketplace_backend.Service.Impl.ProductParametersServiceImpl;
import com.example.marketplace_backend.Service.Impl.ProductSubParametersServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;


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
    private final SubcategoryRepository subcategoryRepository;
    private final SubcategoryServiceImpl subcategoryService;
    private final ProductParametersServiceImpl productParametersService;
    private final ProductSubParametersServiceImpl productSubParametersService;


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

    @PostMapping(value = "/products/edit/{id}", consumes = {"multipart/form-data"})
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

    //Categories ------------------------------------------------------------------------------
    @GetMapping("/categories")
    public ResponseEntity<List<Category>> getAllCategories() {
        return ResponseEntity.ok(categoryService.findAllActive());
    }

    @GetMapping("/categories/inactive")
    public ResponseEntity<List<Category>> getInactiveCategories() {
        return ResponseEntity.ok(categoryService.findAllDeActive());
    }

    @GetMapping("/categories/{id}")
    public ResponseEntity<Category> getCategoryById(@PathVariable UUID id) {
        Optional<Category> category = categoryService.findById(id);
        return category.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/categories/exists/{name}")
    public ResponseEntity<Boolean> categoryExistsByName(@PathVariable String name) {
        return ResponseEntity.ok(categoryService.existsByName(name));
    }

    @GetMapping("/categories/stats")
    public ResponseEntity<Map<String, Long>> getCategoriesStats() {
        Map<String, Long> stats = new HashMap<>();
        stats.put("activeCategories", categoryService.countActiveCategories());
        stats.put("inactiveCategories", categoryService.countDeActiveCategories());
        return ResponseEntity.ok(stats);
    }

    @DeleteMapping("/categories/{id}")
    public ResponseEntity<Void> softDeleteCategory(@PathVariable UUID id) {
        try {
            categoryService.softDelete(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/categories/restore/{id}")
    public ResponseEntity<Category> restoreCategory(@PathVariable UUID id) {
        try {
            categoryService.restore(id);
            Optional<Category> category = categoryService.findById(id);
            return category.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
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


    @DeleteMapping("/categories/{id}/permanent")
    public ResponseEntity<Void> permanentDeleteCategory(@PathVariable UUID id) {
        try {
            categoryService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/categories/purge")
    public ResponseEntity<Void> purgeOldCategories(@RequestParam int daysOld) {
        try {
            LocalDateTime expirationDate = LocalDateTime.now().minusDays(daysOld);
            categoryService.purgeOldCategories(expirationDate);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
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

    @DeleteMapping("/categories/{categoryId}/images/{imageId}")
    public ResponseEntity<Void> deleteCategoryImage(
            @PathVariable UUID categoryId,
            @PathVariable UUID imageId
    ) {
        try {
            Optional<Category> categoryOpt = categoryService.findById(categoryId);
            if (categoryOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            Category category = categoryOpt.get();
            if (category.getCategoryImages() != null) {
                category.getCategoryImages().removeIf(img ->
                        img.getImage() != null && img.getImage().getId().equals(imageId)
                );
                categoryService.save(category);
            }

            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    //Subcategories ------------------------------------------------------------------------------
    @GetMapping("/subcategories")
    public ResponseEntity<List<Subcategory>> getAllSubcategories() {
        return ResponseEntity.ok(subcategoryService.findAllActive());
    }

    @GetMapping("/subcategories/inactive")
    public ResponseEntity<List<Subcategory>> getInactiveSubcategories() {
        return ResponseEntity.ok(subcategoryService.findAllDeActive());
    }

    @GetMapping("/subcategories/{id}")
    public ResponseEntity<Subcategory> getSubcategoryById(@PathVariable UUID id) {
        Optional<Subcategory> subcategory = subcategoryService.findById(id);
        return subcategory.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/subcategories/category/{categoryId}")
    public ResponseEntity<List<Subcategory>> getSubcategoriesByCategory(@PathVariable UUID categoryId) {
        Optional<Category> categoryOpt = categoryService.findById(categoryId);
        return categoryOpt.map(category -> ResponseEntity.ok(subcategoryService.findByCategoryActive(category))).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/subcategories/category/{categoryId}/inactive")
    public ResponseEntity<List<Subcategory>> getInactiveSubcategoriesByCategory(@PathVariable UUID categoryId) {
        Optional<Category> categoryOpt = categoryService.findById(categoryId);
        return categoryOpt.map(category -> ResponseEntity.ok(subcategoryService.findByCategoryDeActive(category))).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/subcategories/stats")
    public ResponseEntity<Map<String, Long>> getSubcategoriesStats() {
        Map<String, Long> stats = new HashMap<>();
        stats.put("activeSubcategories", subcategoryService.countActiveSubcategories());
        stats.put("inactiveSubcategories", subcategoryService.countDeActiveSubcategories());
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/subcategories/stats/category/{categoryId}")
    public ResponseEntity<Map<String, Long>> getSubcategoriesStatsByCategory(@PathVariable UUID categoryId) {
        Map<String, Long> stats = new HashMap<>();
        stats.put("activeSubcategories", subcategoryService.countActiveByCategoryId(categoryId));
        stats.put("inactiveSubcategories", subcategoryService.countDeActiveByCategoryId(categoryId));
        return ResponseEntity.ok(stats);
    }

    @DeleteMapping("/subcategories/{id}")
    public ResponseEntity<Void> softDeleteSubcategory(@PathVariable UUID id) {
        try {
            subcategoryService.softDelete(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/subcategories/restore/{id}")
    public ResponseEntity<Subcategory> restoreSubcategory(@PathVariable UUID id) {
        try {
            subcategoryService.restore(id);
            Optional<Subcategory> subcategory = subcategoryService.findById(id);
            return subcategory.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping(value = "/subcategories/create", consumes = {"multipart/form-data"})
    public ResponseEntity<Subcategory> createSubcategoryWithImages(
            @RequestParam String name,
            @RequestParam UUID categoryId,
            @RequestParam("images") List<MultipartFile> images
    ) throws Exception {

        Optional<Category> categoryOpt = categoryService.findById(categoryId);
        if (categoryOpt.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        Subcategory subcategory = new Subcategory();
        subcategory.setName(name);
        subcategory.setCategory(categoryOpt.get());
        subcategory.setDeletedAt(null);
        subcategory.setCreatedAt(LocalDateTime.now());

        subcategory = subcategoryService.save(subcategory);

        List<SubcategoryImage> subcategoryImages = new ArrayList<>();
        for (MultipartFile image : images) {
            FileEntity savedImage = fileUploadService.saveImage(image);
            SubcategoryImage subcategoryImage = SubcategoryImage.builder()
                    .subcategory(subcategory)
                    .image(savedImage)
                    .build();
            subcategoryImages.add(subcategoryImage);
        }

        subcategory.setSubcategoryImages(subcategoryImages);

        subcategory = subcategoryService.save(subcategory);

        return ResponseEntity.ok(subcategory);
    }

    @DeleteMapping("/subcategories/{id}/permanent")
    public ResponseEntity<Void> permanentDeleteSubcategory(@PathVariable UUID id) {
        try {
            subcategoryService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/subcategories/purge")
    public ResponseEntity<Void> purgeOldSubcategories(@RequestParam int daysOld) {
        try {
            LocalDateTime expirationDate = LocalDateTime.now().minusDays(daysOld);
            subcategoryService.purgeOldSubcategories(expirationDate);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping(value = "/subcategories/edit/{id}", consumes = {"multipart/form-data"})
    public ResponseEntity<Subcategory> editSubcategory(
            @PathVariable UUID id,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) UUID categoryId,
            @RequestParam(name = "images", required = false) List<MultipartFile> images
    ) throws IOException {
        Subcategory subcategory = subcategoryService.getById(id);

        if (name != null && !name.isEmpty()) {
            subcategory.setName(name);
        }

        if (categoryId != null) {
            Optional<Category> categoryOpt = categoryService.findById(categoryId);
            categoryOpt.ifPresent(subcategory::setCategory);
        }

        if (images != null && !images.isEmpty()) {
            List<SubcategoryImage> newSubcategoryImages = images.stream()
                    .map(image -> {
                        try {
                            FileEntity savedImage = fileUploadService.saveImage(image);
                            return SubcategoryImage.builder()
                                    .subcategory(subcategory)
                                    .image(savedImage)
                                    .build();
                        } catch (IOException e) {
                            throw new RuntimeException("Ошибка при сохранении изображения", e);
                        }
                    })
                    .toList();

            if (subcategory.getSubcategoryImages() != null) {
                subcategory.getSubcategoryImages().addAll(newSubcategoryImages);
            } else {
                subcategory.setSubcategoryImages(newSubcategoryImages);
            }
        }

        subcategoryService.save(subcategory);
        return ResponseEntity.ok(subcategory);
    }

    @DeleteMapping("/subcategories/{subcategoryId}/images/{imageId}")
    public ResponseEntity<Void> deleteSubcategoryImage(
            @PathVariable UUID subcategoryId,
            @PathVariable UUID imageId
    ) {
        try {
            Optional<Subcategory> subcategoryOpt = subcategoryService.findById(subcategoryId);
            if (subcategoryOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            Subcategory subcategory = subcategoryOpt.get();
            if (subcategory.getSubcategoryImages() != null) {
                subcategory.getSubcategoryImages().removeIf(img ->
                        img.getImage() != null && img.getImage().getId().equals(imageId)
                );
                subcategoryService.save(subcategory);
            }

            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    // Orders
    @GetMapping("/orders/wholesale")
    public ResponseEntity<List<Order>> getWholesaleOrders() {
        return ResponseEntity.ok(orderService.getAllWholesaleOrders());
    }

    @GetMapping("/orders/retail")
    public ResponseEntity<List<Order>> getRetailOrders() {
        return ResponseEntity.ok(orderService.getAllRetailOrders());
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

    //Brand------------------------------------------------------------------------------------
    @GetMapping("/brands")
    public ResponseEntity<List<Brand>> getAllBrands() {
        return ResponseEntity.ok(brandService.findAllActive());
    }

    @GetMapping("/brands/inactive")
    public ResponseEntity<List<Brand>> getInactiveBrands() {
        return ResponseEntity.ok(brandService.findAllDeActive());
    }

    @GetMapping("/brands/{id}")
    public ResponseEntity<Brand> getBrandById(@PathVariable UUID id) {
        Optional<Brand> brand = brandService.findById(id);
        return brand.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/brands/stats")
    public ResponseEntity<Map<String, Long>> getBrandsStats() {
        Map<String, Long> stats = new HashMap<>();
        stats.put("activeBrands", brandService.countActiveBrands());
        stats.put("inactiveBrands", brandService.countDeActiveBrands());
        return ResponseEntity.ok(stats);
    }

    @DeleteMapping("/brands/{id}")
    public ResponseEntity<Void> softDeleteBrand(@PathVariable UUID id) {
        try {
            brandService.softDelete(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/brands/restore/{id}")
    public ResponseEntity<Brand> restoreBrand(@PathVariable UUID id) {
        try {
            brandService.restore(id);
            Optional<Brand> brand = brandService.findById(id);
            return brand.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
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
        for (MultipartFile image : images) {
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

    @DeleteMapping("/brands/{id}/permanent")
    public ResponseEntity<Void> permanentDeleteBrand(@PathVariable UUID id) {
        try {
            brandService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/brands/purge")
    public ResponseEntity<Void> purgeOldBrands(@RequestParam int daysOld) {
        try {
            LocalDateTime expirationDate = LocalDateTime.now().minusDays(daysOld);
            brandService.purgeOldBrands(expirationDate);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping(value = "/brands/edit/{id}", consumes = {"multipart/form-data"})
    public ResponseEntity<Brand> editBrand(
            @PathVariable UUID id,
            @RequestParam(required = false) String name,
            @RequestParam(name = "images", required = false) List<MultipartFile> images
    ) throws IOException {
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

    @DeleteMapping("/brands/{brandId}/images/{imageId}")
    public ResponseEntity<Void> deleteBrandImage(
            @PathVariable UUID brandId,
            @PathVariable UUID imageId
    ) {
        try {
            Optional<Brand> brandOpt = brandService.findById(brandId);
            if (brandOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            Brand brand = brandOpt.get();
            if (brand.getBrandImages() != null) {
                brand.getBrandImages().removeIf(img ->
                        img.getImage() != null && img.getImage().getId().equals(imageId)
                );
                brandService.save(brand);
            }

            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // ProductParameters ------------------------------------------------------------------------
    @GetMapping("/product-parameters")
    public ResponseEntity<List<ProductParameters>> getAllProductParameters() {
        return ResponseEntity.ok(productParametersService.findAll());
    }

    @GetMapping("/product-parameters/{id}")
    public ResponseEntity<ProductParameters> getProductParameterById(@PathVariable UUID id) {
        Optional<ProductParameters> parameter = productParametersService.findById(id);
        return parameter.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/product-parameters/product/{productId}")
    public ResponseEntity<List<ProductParameters>> getProductParametersByProductId(@PathVariable UUID productId) {
        if (!productParametersService.productExists(productId)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(productParametersService.findByProductId(productId));
    }

    @GetMapping("/product-parameters/stats/product/{productId}")
    public ResponseEntity<Map<String, Long>> getProductParametersStats(@PathVariable UUID productId) {
        if (!productParametersService.productExists(productId)) {
            return ResponseEntity.notFound().build();
        }
        Map<String, Long> stats = new HashMap<>();
        stats.put("parametersCount", productParametersService.countByProductId(productId));
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/product-parameters/search")
    public ResponseEntity<ProductParameters> findProductParameterByNameAndProductId(
            @RequestParam String name,
            @RequestParam UUID productId) {

        if (!productParametersService.productExists(productId)) {
            return ResponseEntity.notFound().build();
        }

        Optional<ProductParameters> parameter = productParametersService.findByNameAndProductId(name, productId);
        return parameter.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/product-parameters/create")
    public ResponseEntity<ProductParameters> createProductParameter(@RequestBody ProductParameters productParameters) {
        try {
            ProductParameters created = productParametersService.create(productParameters);
            return ResponseEntity.ok(created);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/product-parameters/edit/{id}")
    public ResponseEntity<ProductParameters> editProductParameter(
            @PathVariable UUID id,
            @RequestBody ProductParameters updatedParameters) {
        try {
            ProductParameters updated = productParametersService.update(id, updatedParameters);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/product-parameters/{id}")
    public ResponseEntity<Void> deleteProductParameter(@PathVariable UUID id) {
        try {
            productParametersService.deleteById(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/product-parameters/product/{productId}")
    public ResponseEntity<Void> deleteAllProductParametersByProductId(@PathVariable UUID productId) {
        try {
            if (!productParametersService.productExists(productId)) {
                return ResponseEntity.notFound().build();
            }
            productParametersService.deleteByProductId(productId);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    //ProductSubParameter ---------------------------------------------------------------------------
    @GetMapping("/product-sub-parameters")
    public ResponseEntity<List<ProductSubParameters>> getAllProductSubParameters() {
        return ResponseEntity.ok(productSubParametersService.findAll());
    }

    @GetMapping("/product-sub-parameters/{id}")
    public ResponseEntity<ProductSubParameters> getProductSubParameterById(@PathVariable UUID id) {
        Optional<ProductSubParameters> subParameter = productSubParametersService.findById(id);
        return subParameter.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/product-sub-parameters/parameter/{parameterId}")
    public ResponseEntity<List<ProductSubParameters>> getProductSubParametersByParameterId(@PathVariable UUID parameterId) {
        if (!productSubParametersService.productParameterExists(parameterId)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(productSubParametersService.findByProductParameterId(parameterId));
    }

    @GetMapping("/product-sub-parameters/stats/parameter/{parameterId}")
    public ResponseEntity<Map<String, Long>> getProductSubParametersStats(@PathVariable UUID parameterId) {
        if (!productSubParametersService.productParameterExists(parameterId)) {
            return ResponseEntity.notFound().build();
        }
        Map<String, Long> stats = new HashMap<>();
        List<ProductSubParameters> subParams = productSubParametersService.findByProductParameterId(parameterId);
        stats.put("subParametersCount", (long) subParams.size());
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/product-sub-parameters/search")
    public ResponseEntity<ProductSubParameters> findProductSubParameterByNameAndParameterId(
            @RequestParam String name,
            @RequestParam UUID parameterId) {

        if (!productSubParametersService.productParameterExists(parameterId)) {
            return ResponseEntity.notFound().build();
        }

        Optional<ProductSubParameters> subParameter = productSubParametersService.findByNameAndProductParameterId(name, parameterId);
        return subParameter.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/product-sub-parameters/create")
    public ResponseEntity<ProductSubParameters> createProductSubParameter(@RequestBody ProductSubParameters productSubParameters) {
        try {
            ProductSubParameters created = productSubParametersService.create(productSubParameters);
            return ResponseEntity.ok(created);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/product-sub-parameters/create/batch")
    public ResponseEntity<List<ProductSubParameters>> createProductSubParametersBatch(@RequestBody List<ProductSubParameters> productSubParameters) {
        try {
            List<ProductSubParameters> created = productSubParametersService.createBatch(productSubParameters);
            return ResponseEntity.ok(created);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/product-sub-parameters/edit/{id}")
    public ResponseEntity<ProductSubParameters> editProductSubParameter(
            @PathVariable UUID id,
            @RequestBody ProductSubParameters updatedSubParameters) {
        try {
            ProductSubParameters updated = productSubParametersService.update(id, updatedSubParameters);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/product-sub-parameters/edit/{id}/value")
    public ResponseEntity<ProductSubParameters> editProductSubParameterValue(
            @PathVariable UUID id,
            @RequestParam String value) {
        try {
            ProductSubParameters updated = productSubParametersService.updateValue(id, value);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/product-sub-parameters/{id}")
    public ResponseEntity<Void> deleteProductSubParameter(@PathVariable UUID id) {
        try {
            productSubParametersService.deleteById(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/product-sub-parameters/parameter/{parameterId}")
    public ResponseEntity<Void> deleteAllProductSubParametersByParameterId(@PathVariable UUID parameterId) {
        try {
            if (!productSubParametersService.productParameterExists(parameterId)) {
                return ResponseEntity.notFound().build();
            }
            productSubParametersService.deleteByProductParameterId(parameterId);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}