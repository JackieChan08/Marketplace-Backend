package com.example.marketplace_backend.Service.Impl;

import com.example.marketplace_backend.Model.Category;
import com.example.marketplace_backend.Model.FileEntity;
import com.example.marketplace_backend.Model.Product;
import com.example.marketplace_backend.Model.Intermediate_objects.ProductImage;
import com.example.marketplace_backend.Repositories.BrandRepository;
import com.example.marketplace_backend.Repositories.CategoryRepository;
import com.example.marketplace_backend.Repositories.ProductImageRepository;
import com.example.marketplace_backend.Repositories.ProductRepository;
import com.example.marketplace_backend.controller.Requests.models.ProductRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.security.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class ProductServiceImpl extends BaseServiceImpl<Product, UUID> {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final FileUploadService fileUploadService;
    private final ProductImageRepository productImageRepository;
    private final BrandRepository brandRepository;

    @Autowired
    public ProductServiceImpl(ProductRepository productRepository,
                              CategoryRepository categoryRepository,
                              FileUploadService fileUploadService,
                              ProductImageRepository productImageRepository, BrandRepository brandRepository) {
        super(productRepository);
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.fileUploadService = fileUploadService;
        this.productImageRepository = productImageRepository;
        this.brandRepository = brandRepository;
    }

    public Product createProduct(ProductRequest dto) throws Exception {
        Product product = new Product();
        product.setName(dto.getName());
        product.setPrice(dto.getPrice());
        product.setDescriptions(dto.getDescriptions());
        product.setCategory(categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found")));
        product.setBrand(brandRepository.findById(dto.getBrandId())
                .orElseThrow(() -> new RuntimeException("Brand not found")));

        Product savedProduct = productRepository.save(product);

        if (dto.getImages() != null) {
            for (MultipartFile image : dto.getImages()) {
                FileEntity fileEntity = fileUploadService.saveImage(image);
                ProductImage productImage = ProductImage.builder()
                        .product(savedProduct)
                        .image(fileEntity)
                        .build();
                productImageRepository.save(productImage);
            }
        }

        return savedProduct;
    }

    public Product editProduct(UUID id, ProductRequest dto) throws Exception {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        if (dto.getName() != null) product.setName(dto.getName());
        if (dto.getPrice() != null) product.setPrice(dto.getPrice());
        if (dto.getDescriptions() != null) product.setDescriptions(dto.getDescriptions());
        if (dto.getCategoryId() != null) {
            product.setCategory(categoryRepository.findById(dto.getCategoryId())
                    .orElseThrow(() -> new RuntimeException("Category not found")));
        }
        if (dto.getBrandId() != null) {
            product.setBrand(brandRepository.findById(dto.getBrandId())
                    .orElseThrow(() -> new RuntimeException("Brand not found")));
        }
        product.setCreatedAt(LocalDateTime.now());

        Product updatedProduct = productRepository.save(product);

        if (dto.getImages() != null && !dto.getImages().isEmpty()) {
            productImageRepository.deleteByProductId(product.getId());
            for (MultipartFile image : dto.getImages()) {
                FileEntity fileEntity = fileUploadService.saveImage(image);
                ProductImage productImage = ProductImage.builder()
                        .product(updatedProduct)
                        .image(fileEntity)
                        .build();
                productImageRepository.save(productImage);
            }
        }

        return updatedProduct;
    }


    public Product findByName(String name) {
        return productRepository.findByName(name);
    }

    public List<Product> findByNameContaining(String name) {
        return productRepository.findByNameContaining(name);
    }
    public List<Product> findByCategory(Category category){
        return productRepository.findByCategory(category);
    };
    public void deActiveProductByCategory(Category category){
        List<Product> products = productRepository.findByCategory(category);
        for(Product product : products){
            product.setDeletedAt(LocalDateTime.now());
            productRepository.save(product);
        }
    }
    public void activeProductByCategory(Category category){
        List<Product> products = productRepository.findByCategory(category);
        for(Product product : products){
            product.setDeletedAt(null);
            productRepository.save(product);
        }
    }
    public List<Product> findAllDeActive() {
        return productRepository.findAllDeActive();
    }
    public List<Product> findAllActive() {
        return productRepository.findAllActive();
    }

    public Product createProduct(Product product) {
        return productRepository.save(product);
    }

    public List<Product> findAll() {
        return productRepository.findAll();
    }
}
