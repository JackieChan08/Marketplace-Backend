package com.example.marketplace_backend.Service.Impl;

import com.example.marketplace_backend.Model.*;
import com.example.marketplace_backend.Model.Intermediate_objects.ProductImage;
import com.example.marketplace_backend.Repositories.*;
import com.example.marketplace_backend.DTO.Requests.models.ProductRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class ProductServiceImpl extends BaseServiceImpl<Product, UUID> {
    private final ProductRepository productRepository;
    private final FileUploadService fileUploadService;
    private final ProductImageRepository productImageRepository;
    private final BrandRepository brandRepository;
    private final SubcategoryRepository subcategoryRepository;

    @Autowired
    public ProductServiceImpl(ProductRepository productRepository,
                              SubcategoryRepository subcategoryRepository,
                              FileUploadService fileUploadService,
                              ProductImageRepository productImageRepository, BrandRepository brandRepository) {
        super(productRepository);
        this.productRepository = productRepository;
        this.fileUploadService = fileUploadService;
        this.productImageRepository = productImageRepository;
        this.brandRepository = brandRepository;
        this.subcategoryRepository = subcategoryRepository;
    }

    public Product createProduct(ProductRequest dto) throws Exception {
        Product product = new Product();
        product.setName(dto.getName());
        product.setPrice(dto.getPrice());
        product.setDescriptions(dto.getDescriptions());
        product.setSubcategory(subcategoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found")));
        product.setBrand(brandRepository.findById(dto.getBrandId())
                .orElseThrow(() -> new RuntimeException("Brand not found")));
        product.setAvailability(dto.isAvailability());

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
        if (dto.isAvailability()) product.setAvailability(true);
        if (dto.getDescriptions() != null) product.setDescriptions(dto.getDescriptions());
        if (dto.getCategoryId() != null) {
            product.setSubcategory(subcategoryRepository.findById(dto.getCategoryId())
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

    public Page<Product> findByNameContaining(String name, Pageable pageable) {
        return productRepository.findByNameContaining(name, pageable);
    }
    public List<Product> findBySubcategory(Subcategory subcategory){
        return productRepository.findBySubcategoryAndDeletedAtIsNull(subcategory);
    };

    public void deActiveProductBySubcategory(Subcategory subcategory){
        List<Product> products = productRepository.findBySubcategoryAndDeletedAtIsNull(subcategory);
        for(Product product : products){
            product.setDeletedAt(LocalDateTime.now());
            productRepository.save(product);
        }
    }

    public void activeProductByCategory(Subcategory subcategory){
        List<Product> products = productRepository.findBySubcategoryAndDeletedAtIsNotNull(subcategory);
        for(Product product : products){
            product.setDeletedAt(null);
            productRepository.save(product);
        }
    }

    public void deActiveProductByBrand(Brand brand){
        List<Product> products = productRepository.findByBrandAndDeletedAtIsNull(brand);
        for(Product product : products){
            product.setDeletedAt(LocalDateTime.now());
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


    @Override
    public void delete(UUID id) {
        List<ProductImage>  productImages = productImageRepository.findByProductId(id);
        for (ProductImage productImage : productImages) {
            fileUploadService.deleteImage(productImage.getImage().getUniqueName());
        }
        productRepository.deleteById(id);
    }

    public Page<Product> findAll(Pageable pageable) {
        return productRepository.findAll(pageable);
    }


}
