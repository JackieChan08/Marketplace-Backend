package com.example.marketplace_backend.Service.Impl;

import com.example.marketplace_backend.DTO.Responses.models.FileResponse;
import com.example.marketplace_backend.DTO.Responses.models.ProductResponse;
import com.example.marketplace_backend.Model.*;
import com.example.marketplace_backend.Model.Intermediate_objects.ProductImage;
import com.example.marketplace_backend.Repositories.*;
import com.example.marketplace_backend.DTO.Requests.models.ProductRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ProductServiceImpl extends BaseServiceImpl<Product, UUID> {
    private final ProductRepository productRepository;
    private final FileUploadService fileUploadService;
    private final ProductImageRepository productImageRepository;
    private final BrandRepository brandRepository;
    private final SubcategoryRepository subcategoryRepository;

    @Value("${app.base-url}")
    private static String baseUrl;

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

    public List<Product> findAllDeActive() {
        return productRepository.findAllDeActive();
    }

    public List<Product> findAllActive() {
        return productRepository.findAllActive();
    }

    public Product findByName(String name) {
        return productRepository.findByName(name);
    }

    public boolean existsByName(String name) {
        return productRepository.findByName(name) != null;
    }

    public long countActiveProducts() {
        return productRepository.findAllActive().size();
    }

    public long countDeActiveProducts() {
        return productRepository.findAllDeActive().size();
    }

    public Page<Product> findByNameContaining(String name, Pageable pageable) {
        return productRepository.findByNameContaining(name, pageable);
    }

    public List<Product> findAllActiveBySubcategory(Subcategory subcategory){
        return productRepository.findActiveBySubcategory(subcategory);
    }

    public List<Product> findAllDeActiveBySubcategory(Subcategory subcategory){
        return productRepository.findDeActiveBySubcategory(subcategory);
    }

    public List<Product> findAllActiveByBrand(Brand brand){
        return productRepository.findActiveByBrand(brand);
    }

    public List<Product> findAllDeActiveByBrand(Brand brand){
        return productRepository.findDeActiveByBrand(brand);
    }

    public Page<Product> findAll(Pageable pageable) {
        return productRepository.findAll(pageable);
    }

    public void deActiveProductsBySubcategory(Subcategory subcategory){
        List<Product> products = productRepository.findActiveBySubcategory(subcategory);
        for(Product product : products){
            product.setDeletedAt(LocalDateTime.now());
            productRepository.save(product);
        }
    }

    public void activeProductsByCategory(Subcategory subcategory){
        List<Product> products = productRepository.findDeActiveBySubcategory(subcategory);
        for(Product product : products){
            product.setDeletedAt(null);
            productRepository.save(product);
        }
    }

    public void deActiveProductsByBrand(Brand brand){
        List<Product> products = productRepository.findActiveByBrand(brand);
        for(Product product : products){
            product.setDeletedAt(LocalDateTime.now());
            productRepository.save(product);
        }
    }

    public void activeProductsByBrand(Brand brand){
        List<Product> products = productRepository.findActiveByBrand(brand);
        for(Product product : products){
            product.setDeletedAt(LocalDateTime.now());
            productRepository.save(product);
        }
    }

    public Optional<Product> findById(UUID productId) {
        return productRepository.findById(productId);
    }

    @Override
    public Product save(Product product) {
        if (product.getId() != null) {
            product.setCreatedAt(LocalDateTime.now());
        } else {
            product.setUpdatedAt(LocalDateTime.now());
        }
        return productRepository.save(product);
    }

    public void softDelete(UUID id) {
        productRepository.softDeleteById(id, LocalDateTime.now());
    }

    public void restore(UUID id) {
        Optional<Product> productOptional = productRepository.findById(id);
        if (productOptional.isPresent()) {
            Product product = productOptional.get();
            product.setDeletedAt(null);
            product.setUpdatedAt(LocalDateTime.now());
            productRepository.save(product);
        }
    }

    public void purgeDeletedProducts() {
        List<Product> productsToDelete = productRepository.findAllDeActive();
        productRepository.deleteAll(productsToDelete);
    }

    public Product createProduct(ProductRequest dto) throws Exception {
        Product product = new Product();
        product.setName(dto.getName());
        product.setPrice(dto.getPrice());
        product.setDescriptions(dto.getDescriptions());
        product.setSubcategory(subcategoryRepository.findById(dto.getSubCategoryId())
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
//        if (dto.getDescriptions() != null) product.setDescriptions(dto.getDescriptions());
        if (dto.getSubCategoryId() != null) {
            product.setSubcategory(subcategoryRepository.findById(dto.getSubCategoryId())
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

    @Override
    public void delete(UUID id) {
        try {
            List<ProductImage>  productImages = productImageRepository.findByProductId(id);

            for (ProductImage productImage : productImages) {
                if (productImage.getImage() != null && productImage.getImage().getUniqueName() != null) {
                    fileUploadService.deleteImage(productImage.getImage().getUniqueName());
                }
            }

            productRepository.deleteById(id);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при удалении продукта с ID: " + id, e);
        }
    }

    public boolean deleteProductImage(UUID productId, UUID imageId) {
        Optional<Product> productOpt = productRepository.findById(productId);

        if (productOpt.isEmpty()) {
            return false;
        }

        Product product = productOpt.get();

        boolean removed = false;
        if(product.getProductImages() != null) {
            removed = product.getProductImages().removeIf(img ->
                    img.getImage() != null && img.getProduct().getId().equals(productId));
        }

        if (removed) {
            productRepository.save(product);
        }

        return removed;
    }

    public static ProductResponse convertToProductResponse(Product product) {
        ProductResponse response = new ProductResponse();
        response.setId(product.getId());
        response.setName(product.getName());
        response.setDescriptions(product.getDescriptions());

        // Получаем подкатегорию
        Subcategory subcategory = product.getSubcategory();
        if (subcategory != null) {
            response.setSubcategoryId(subcategory.getId());
            response.setSubcategoryName(subcategory.getName());

            // Получаем категорию через подкатегорию
            if (subcategory.getCategory() != null) {
                response.setCategoryId(subcategory.getCategory().getId());
                response.setCategoryName(subcategory.getCategory().getName());
            }
        }

        // Бренд
        if (product.getBrand() != null) {
            response.setBrandId(product.getBrand().getId());
        }

        // Изображения
        if (product.getProductImages() != null && !product.getProductImages().isEmpty()) {
            List<FileResponse> images = product.getProductImages().stream()
                    .map(productImage -> {
                        FileEntity image = productImage.getImage();
                        FileResponse fileResponse = new FileResponse();
                        fileResponse.setUniqueName(image.getUniqueName());
                        fileResponse.setOriginalName(image.getOriginalName());
                        fileResponse.setUrl(baseUrl + "/uploads/" + image.getUniqueName());
                        fileResponse.setFileType(image.getFileType());
                        return fileResponse;
                    })
                    .toList();

            response.setImages(images);
        }

        return response;
    }
}
