package com.example.marketplace_backend.Service.Impl;

import com.example.marketplace_backend.DTO.Requests.models.ProductFilterRequest;
import com.example.marketplace_backend.DTO.Requests.models.ProductParameterRequest;
import com.example.marketplace_backend.DTO.Requests.models.ProductRequest;
import com.example.marketplace_backend.DTO.Requests.models.ProductSubParameterRequest;
import com.example.marketplace_backend.DTO.Responses.models.ProductResponse;
import com.example.marketplace_backend.Model.*;
import com.example.marketplace_backend.Model.Intermediate_objects.ProductImage;
import com.example.marketplace_backend.Model.Intermediate_objects.ProductStatuses;
import com.example.marketplace_backend.Repositories.*;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
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
    private final StatusRepository statusRepository;
    private final ProductStatusRepository productStatusRepository;
    private final ConverterService converter;
    private final ProductParametersRepository productParametersRepository;

    @Value("${app.base-url}")
    private String baseUrl;

    @Autowired
    public ProductServiceImpl(ProductRepository productRepository,
                              SubcategoryRepository subcategoryRepository,
                              FileUploadService fileUploadService,
                              ProductImageRepository productImageRepository,
                              BrandRepository brandRepository,
                              ProductStatusRepository productStatusRepository,
                              StatusRepository statusRepository,
                              ConverterService converter, ProductParametersRepository pproductParametersRepository, ProductParametersRepository productParametersRepository) {
        super(productRepository);
        this.productRepository = productRepository;
        this.fileUploadService = fileUploadService;
        this.productImageRepository = productImageRepository;
        this.brandRepository = brandRepository;
        this.subcategoryRepository = subcategoryRepository;
        this.statusRepository = statusRepository;
        this.productStatusRepository = productStatusRepository;
        this.converter = converter;
        this.productParametersRepository = productParametersRepository;
    }

    public Page<Product> findAllActiveByBrand(UUID brandId, Pageable pageable) {
        return productRepository.findActiveByBrand(brandId, pageable);
    }
    public Page<Product> findAllActiveBySubcategory(UUID subcategoryId, Pageable pageable) {
        return productRepository.findActiveBySubcategory(subcategoryId, pageable);
    }

    public Page<Product> findAllActiveByCategoryId(UUID categoryId, Pageable pageable) {
        return productRepository.findActiveByCategory(categoryId, pageable);
    }

    public List<Product> findAllDeActive() {
        return productRepository.findAllDeActive();
    }

    public Page<Product> findAllDeActive(Pageable pageable) {
        return productRepository.findAllDeActive(pageable);
    }

    public List<Product> findAllActive() {
        return productRepository.findAllActive();
    }

    public Page<Product> findAllActive(Pageable pageable) {
        return productRepository.findAllActive(pageable);
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
    public Page<Product> findByNameContainingActive(String name, Pageable pageable) {
        return productRepository.findByNameContainingActive(name, pageable);
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
        List<Product> products = productRepository.findDeActiveByBrand(brand);
        for(Product product : products){
            product.setDeletedAt(null);
            productRepository.save(product);
        }
    }

    public Optional<Product> findById(UUID productId) {
        return productRepository.findById(productId);
    }

    @Override
    public Product save(Product product) {
        if (product.getId() == null) {
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
        product.setSubcategory(subcategoryRepository.findById(dto.getSubCategoryId())
                .orElseThrow(() -> new RuntimeException("Subcategory not found")));
        product.setBrand(brandRepository.findById(dto.getBrandId())
                .orElseThrow(() -> new RuntimeException("Brand not found")));
        product.setAvailability(dto.isAvailability());
        product.setTitle(dto.getTitle());
        product.setDescription(dto.getDescription());

        Product savedProduct = productRepository.save(product);

        // Обрабатываем изображения
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

        // Обрабатываем статусы
        if (dto.getStatusId() != null && !dto.getStatusId().isEmpty()) {
            for (UUID statusId : dto.getStatusId()) {
                Statuses status = statusRepository.findById(statusId)
                        .orElseThrow(() -> new RuntimeException("Status not found with ID: " + statusId));

                ProductStatuses productStatus = ProductStatuses.builder()
                        .product(savedProduct)
                        .status(status)
                        .build();
                productStatusRepository.save(productStatus);
            }
        } else {
            // Устанавливаем статус по умолчанию, если не указан
            Statuses defaultStatus = statusRepository.findByName("Активен")
                    .orElseThrow(() -> new RuntimeException("Default status not found"));

            ProductStatuses productStatus = ProductStatuses.builder()
                    .product(savedProduct)
                    .status(defaultStatus)
                    .build();
            productStatusRepository.save(productStatus);
        }

        ObjectMapper mapper = new ObjectMapper();
        List<ProductParameterRequest> parameters = new ArrayList<>();

        if (dto.getParametersJson() != null && !dto.getParametersJson().isEmpty()) {
            parameters = mapper.readValue(
                    dto.getParametersJson(),
                    new TypeReference<List<ProductParameterRequest>>() {}
            );
        }


        if (parameters != null) {
            for (ProductParameterRequest paramReq : parameters) {
                ProductParameters parameter = new ProductParameters();
                parameter.setName(paramReq.getName());
                parameter.setProduct(product);

                List<ProductSubParameters> subParams = new ArrayList<>();
                for (ProductSubParameterRequest sub : paramReq.getSubParameters()) {
                    ProductSubParameters s = new ProductSubParameters();
                    s.setName(sub.getName());
                    s.setValue(sub.getValue());
                    s.setProductParameter(parameter);
                    subParams.add(s);
                }

                parameter.setProductSubParameters(subParams);
                productParametersRepository.save(parameter);
            }
        }


        Product finalProduct = productRepository.findByIdWithImagesAndStatuses(savedProduct.getId())
                .orElseThrow(() -> new RuntimeException("Product not found after creation"));

        return finalProduct;
    }

    public Product editProduct(UUID id, ProductRequest dto) throws Exception {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        if (dto.getName() != null) product.setName(dto.getName());
        if (dto.getPrice() != null) product.setPrice(dto.getPrice());
        if (dto.getTitle() != null) product.setTitle(dto.getTitle());
        if (dto.getDescription() != null) product.setDescription(dto.getDescription());
        product.setAvailability(dto.isAvailability());

        if (dto.getSubCategoryId() != null) {
            product.setSubcategory(subcategoryRepository.findById(dto.getSubCategoryId())
                    .orElseThrow(() -> new RuntimeException("Subcategory not found")));
        }
        if (dto.getBrandId() != null) {
            product.setBrand(brandRepository.findById(dto.getBrandId())
                    .orElseThrow(() -> new RuntimeException("Brand not found")));
        }

        // Обновляем статусы продукта, если указаны новые
        if (dto.getStatusId() != null && !dto.getStatusId().isEmpty()) {
            productStatusRepository.deleteByProductId(product.getId());

            for (UUID statusId : dto.getStatusId()) {
                Statuses status = statusRepository.findById(statusId)
                        .orElseThrow(() -> new RuntimeException("Status not found with ID: " + statusId));

                ProductStatuses productStatus = ProductStatuses.builder()
                        .product(product)
                        .status(status)
                        .build();
                productStatusRepository.save(productStatus);
            }
        }

        product.setUpdatedAt(LocalDateTime.now());
        Product updatedProduct = productRepository.save(product);

        // Обрабатываем изображения
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
        ObjectMapper mapper = new ObjectMapper();
        List<ProductParameterRequest> parameters = new ArrayList<>();

        if (dto.getParametersJson() != null && !dto.getParametersJson().isEmpty()) {
            parameters = mapper.readValue(
                    dto.getParametersJson(),
                    new TypeReference<List<ProductParameterRequest>>() {}
            );
        }


        if (parameters != null) {
            for (ProductParameterRequest paramReq : parameters) {
                ProductParameters parameter = new ProductParameters();
                parameter.setName(paramReq.getName());
                parameter.setProduct(product);

                List<ProductSubParameters> subParams = new ArrayList<>();
                for (ProductSubParameterRequest sub : paramReq.getSubParameters()) {
                    ProductSubParameters s = new ProductSubParameters();
                    s.setName(sub.getName());
                    s.setValue(sub.getValue());
                    s.setProductParameter(parameter);
                    subParams.add(s);
                }

                parameter.setProductSubParameters(subParams);
                productParametersRepository.save(parameter);
            }
        }

        // ИСПРАВЛЕНИЕ: Используем метод с JOIN FETCH для получения всех связанных данных
        Product finalProduct = productRepository.findByIdWithImagesAndStatuses(updatedProduct.getId())
                .orElseThrow(() -> new RuntimeException("Product not found after update"));

        return finalProduct;
    }

    @Override
    public void delete(UUID id) {
        try {
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

    public Page<ProductResponse> filterProducts(ProductFilterRequest filterRequest) {
        Specification<Product> spec =
                ProductSpecification.isNotDeleted()
                        .and(ProductSpecification.hasSubcategoryIds(filterRequest.getSubcategoryIds()))
                        .and(ProductSpecification.hasBrandIds(filterRequest.getBrandIds()))
                        .and(ProductSpecification.hasPriceBetween(filterRequest.getMinPrice(), filterRequest.getMaxPrice()));

        // Сортировка
        String sortBy = filterRequest.getSortBy() != null ? filterRequest.getSortBy() : "name";
        String direction = filterRequest.getSortDirection() != null ? filterRequest.getSortDirection() : "asc";
        Sort sort = direction.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(filterRequest.getPage(), filterRequest.getSize(), sort);

        Page<Product> productPage = productRepository.findAll(spec, pageable);

        return productPage.map(converter::convertToProductResponse);
    }

}