package com.example.marketplace_backend.Service.Impl;

import com.example.marketplace_backend.DTO.Requests.models.*;
import com.example.marketplace_backend.DTO.Responses.models.*;
import com.example.marketplace_backend.Model.*;
import com.example.marketplace_backend.Model.Intermediate_objects.ProductColorImage;
import com.example.marketplace_backend.Model.Intermediate_objects.ProductImage;
import com.example.marketplace_backend.Model.Intermediate_objects.ProductStatuses;
import com.example.marketplace_backend.Model.ProductSpec.LaptopSpec;
import com.example.marketplace_backend.Model.ProductSpec.PhoneSpec;
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
import org.springframework.transaction.annotation.Transactional;
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
    private final BrandRepository brandRepository;
    private final SubcategoryRepository subcategoryRepository;
    private final StatusRepository statusRepository;
    private final ProductStatusRepository productStatusRepository;
    private final ConverterService converter;
    private final ProductImageRepository productImageRepository;
    private final ProductParametersRepository productParametersRepository;
    private final ProductColorRepository productColorRepository;
    private final ProductColorImageRepository productColorImageRepository;
    private final PhoneSpecRepository phoneSpecRepository;
    private final LaptopSpecRepository laptopSpecRepository;
    private final ProductVariantRepository productVariantRepository;

    @Value("${app.base-url}")
    private String baseUrl;

    @Autowired
    public ProductServiceImpl(ProductRepository productRepository,
                              SubcategoryRepository subcategoryRepository,
                              FileUploadService fileUploadService,
                              ProductColorImageRepository productColorImageRepository,
                              BrandRepository brandRepository,
                              ProductStatusRepository productStatusRepository,
                              StatusRepository statusRepository,
                              ConverterService converter,
                              ProductParametersRepository productParametersRepository,
                              ProductColorRepository productColorRepository,
                              ProductImageRepository productImageRepository,
                              PhoneSpecRepository phoneSpecRepository,
                              LaptopSpecRepository laptopSpecRepository,
                              ProductVariantRepository productVariantRepository) {
        super(productRepository);
        this.productRepository = productRepository;
        this.fileUploadService = fileUploadService;
        this.brandRepository = brandRepository;
        this.subcategoryRepository = subcategoryRepository;
        this.statusRepository = statusRepository;
        this.productStatusRepository = productStatusRepository;
        this.converter = converter;
        this.productParametersRepository = productParametersRepository;
        this.productColorRepository = productColorRepository;
        this.productColorImageRepository = productColorImageRepository;
        this.productImageRepository = productImageRepository;
        this.phoneSpecRepository = phoneSpecRepository;
        this.laptopSpecRepository = laptopSpecRepository;
        this.productVariantRepository = productVariantRepository;
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

    public Page<Product> findAllByStatus(UUID statusId, Pageable pageable) {
        return productRepository.findAllByStatusId(statusId, pageable);
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

    @Transactional
    public Product createProduct(ProductRequest request) throws Exception {
        // Create base product
        Product product = Product.builder()
                .name(request.getName())
                .price(request.getPrice())
                .priceDescription(request.getPriceDescription())
                .title(request.getTitle())
                .description(request.getDescription())
                .availability(request.isAvailability())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        // Set subcategory
        if (request.getSubCategoryId() != null) {
            product.setSubcategory(subcategoryRepository.findById(request.getSubCategoryId())
                    .orElseThrow(() -> new RuntimeException("Subcategory not found")));
        }

        // Set brand
        if (request.getBrandId() != null) {
            product.setBrand(brandRepository.findById(request.getBrandId())
                    .orElseThrow(() -> new RuntimeException("Brand not found")));
        }

        Product savedProduct = productRepository.save(product);

        // Process statuses
        if (request.getStatusId() != null && !request.getStatusId().isEmpty()) {
            for (UUID statusId : request.getStatusId()) {
                Statuses status = statusRepository.findById(statusId)
                        .orElseThrow(() -> new RuntimeException("Status not found with ID: " + statusId));

                ProductStatuses productStatus = ProductStatuses.builder()
                        .product(savedProduct)
                        .status(status)
                        .build();
                productStatusRepository.save(productStatus);
            }
        }

        // Process variants (colors, specifications)
        if (request.getVariants() != null && !request.getVariants().isEmpty()) {
            for (VariantRequest variantRequest : request.getVariants()) {
                ProductVariant variant = new ProductVariant();
                variant.setProduct(savedProduct);

                // Process color
                if (variantRequest.getColor() != null) {
                    ColorRequest colorReq = variantRequest.getColor();
                    ProductColor color = ProductColor.builder()
                            .name(colorReq.getName())
                            .hex(colorReq.getHex())
                            .build();
                    ProductColor savedColor = productColorRepository.save(color);

                    // Add color images
                    if (colorReq.getImages() != null && !colorReq.getImages().isEmpty()) {
                        for (MultipartFile imageFile : colorReq.getImages()) {
                            FileEntity fileEntity = fileUploadService.saveImage(imageFile);
                            ProductColorImage colorImage = ProductColorImage.builder()
                                    .color(savedColor)
                                    .image(fileEntity)
                                    .build();
                            productColorImageRepository.save(colorImage);
                        }
                    }
                    variant.setColor(savedColor);
                }

                // Process phone specifications
                if (variantRequest.getPhoneSpec() != null) {
                    PhoneSpecRequest specReq = variantRequest.getPhoneSpec();
                    PhoneSpec spec = PhoneSpec.builder()
                            .memory(specReq.getMemory())
                            .price(specReq.getPrice())
                            .simType(specReq.getSimType())
                            .build();
                    PhoneSpec savedSpec = phoneSpecRepository.save(spec);
                    variant.setPhoneSpec(savedSpec);
                }

                // Process laptop specifications
                if (variantRequest.getLaptopSpec() != null) {
                    LaptopSpecRequest specReq = variantRequest.getLaptopSpec();
                    LaptopSpec spec = LaptopSpec.builder()
                            .ssdMemory(specReq.getSsdMemory())
                            .price(specReq.getPrice())
                            .build();
                    LaptopSpec savedSpec = laptopSpecRepository.save(spec);
                    variant.setLaptopSpec(savedSpec);
                }

                productVariantRepository.save(variant);
            }
        } else {
            ProductVariant variant = new ProductVariant();
            variant.setProduct(savedProduct);
            productVariantRepository.save(variant);
        }

        // Process general product images if no variants with colors
        if ((request.getVariants() == null || request.getVariants().isEmpty()) &&
                request.getImages() != null && !request.getImages().isEmpty()) {
            for (MultipartFile imageFile : request.getImages()) {
                FileEntity fileEntity = fileUploadService.saveImage(imageFile);
                ProductImage productImage = ProductImage.builder()
                        .product(savedProduct)
                        .image(fileEntity)
                        .build();
                productImageRepository.save(productImage);
            }
        }

        // Process parameters
        ObjectMapper mapper = new ObjectMapper();
        List<ProductParameterRequest> parameters = new ArrayList<>();
        if (request.getParametersJson() != null && !request.getParametersJson().isEmpty()) {
            parameters = mapper.readValue(
                    request.getParametersJson(),
                    new TypeReference<List<ProductParameterRequest>>() {}
            );
        }

        if (parameters != null && !parameters.isEmpty()) {
            for (ProductParameterRequest paramReq : parameters) {
                ProductParameters parameter = new ProductParameters();
                parameter.setName(paramReq.getName());
                parameter.setProduct(savedProduct);

                List<ProductSubParameters> subParams = new ArrayList<>();
                if (paramReq.getSubParameters() != null) {
                    for (ProductSubParameterRequest sub : paramReq.getSubParameters()) {
                        ProductSubParameters s = new ProductSubParameters();
                        s.setName(sub.getName());
                        s.setValue(sub.getValue());
                        s.setProductParameter(parameter);
                        subParams.add(s);
                    }
                }

                parameter.setProductSubParameters(subParams);
                productParametersRepository.save(parameter);
            }
        }

        // Return created product
        return productRepository.findByIdWithImagesAndStatuses(savedProduct.getId())
                .orElseThrow(() -> new RuntimeException("Product not found after creation"));
    }

    @Transactional
    public Product editProduct(UUID id, ProductRequest dto) throws Exception {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        // Update basic fields
        if (dto.getName() != null) product.setName(dto.getName());
        if (dto.getPrice() != null) product.setPrice(dto.getPrice());
        if (dto.getPriceDescription() != null) product.setPriceDescription(dto.getPriceDescription());
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

        // Update statuses
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

        // Update variants
        if (dto.getVariants() != null) {
            // Delete existing variants and related data
            List<ProductVariant> existingVariants = productVariantRepository.findByProductId(product.getId());
            for (ProductVariant variant : existingVariants) {
                if (variant.getColor() != null) {
                    // Delete color images
                    productColorImageRepository.deleteByColorId(variant.getColor().getId());
                    // Delete color
                    productColorRepository.deleteById(variant.getColor().getId());
                }
                if (variant.getPhoneSpec() != null) {
                    phoneSpecRepository.deleteById(variant.getPhoneSpec().getId());
                }
                if (variant.getLaptopSpec() != null) {
                    laptopSpecRepository.deleteById(variant.getLaptopSpec().getId());
                }
            }
            productVariantRepository.deleteByProductId(product.getId());

            // Create new variants
            for (VariantRequest variantRequest : dto.getVariants()) {
                ProductVariant variant = new ProductVariant();
                variant.setProduct(updatedProduct);

                // Process color
                if (variantRequest.getColor() != null) {
                    ColorRequest colorReq = variantRequest.getColor();
                    ProductColor color = ProductColor.builder()
                            .name(colorReq.getName())
                            .hex(colorReq.getHex())
                            .build();
                    ProductColor savedColor = productColorRepository.save(color);

                    // Add color images
                    if (colorReq.getImages() != null && !colorReq.getImages().isEmpty()) {
                        for (MultipartFile imageFile : colorReq.getImages()) {
                            FileEntity fileEntity = fileUploadService.saveImage(imageFile);
                            ProductColorImage colorImage = ProductColorImage.builder()
                                    .color(savedColor)
                                    .image(fileEntity)
                                    .build();
                            productColorImageRepository.save(colorImage);
                        }
                    }
                    variant.setColor(savedColor);
                }

                // Process specifications
                if (variantRequest.getPhoneSpec() != null) {
                    PhoneSpecRequest specReq = variantRequest.getPhoneSpec();
                    PhoneSpec spec = PhoneSpec.builder()
                            .memory(specReq.getMemory())
                            .price(specReq.getPrice())
                            .simType(specReq.getSimType())
                            .build();
                    PhoneSpec savedSpec = phoneSpecRepository.save(spec);
                    variant.setPhoneSpec(savedSpec);
                }

                if (variantRequest.getLaptopSpec() != null) {
                    LaptopSpecRequest specReq = variantRequest.getLaptopSpec();
                    LaptopSpec spec = LaptopSpec.builder()
                            .ssdMemory(specReq.getSsdMemory())
                            .price(specReq.getPrice())
                            .build();
                    LaptopSpec savedSpec = laptopSpecRepository.save(spec);
                    variant.setLaptopSpec(savedSpec);
                }

                productVariantRepository.save(variant);
            }
        } else if (dto.getImages() != null && !dto.getImages().isEmpty()) {
            // If no variants but has general images
            productImageRepository.deleteByProductId(product.getId());
            for (MultipartFile imageFile : dto.getImages()) {
                FileEntity fileEntity = fileUploadService.saveImage(imageFile);
                ProductImage productImage = ProductImage.builder()
                        .product(updatedProduct)
                        .image(fileEntity)
                        .build();
                productImageRepository.save(productImage);
            }
        }

        // Update parameters
        ObjectMapper mapper = new ObjectMapper();
        List<ProductParameterRequest> parameters = new ArrayList<>();
        if (dto.getParametersJson() != null && !dto.getParametersJson().isEmpty()) {
            parameters = mapper.readValue(
                    dto.getParametersJson(),
                    new TypeReference<List<ProductParameterRequest>>() {}
            );
        }

        if (parameters != null && !parameters.isEmpty()) {
            productParametersRepository.deleteByProductId(product.getId());

            for (ProductParameterRequest paramReq : parameters) {
                ProductParameters parameter = new ProductParameters();
                parameter.setName(paramReq.getName());
                parameter.setProduct(updatedProduct);

                List<ProductSubParameters> subParams = new ArrayList<>();
                if (paramReq.getSubParameters() != null) {
                    for (ProductSubParameterRequest sub : paramReq.getSubParameters()) {
                        ProductSubParameters s = new ProductSubParameters();
                        s.setName(sub.getName());
                        s.setValue(sub.getValue());
                        s.setProductParameter(parameter);
                        subParams.add(s);
                    }
                }

                parameter.setProductSubParameters(subParams);
                productParametersRepository.save(parameter);
            }
        }

        return productRepository.findByIdWithImagesAndStatuses(updatedProduct.getId())
                .orElseThrow(() -> new RuntimeException("Product not found after update"));
    }

    @Override
    public void delete(UUID id) {
        try {
            productRepository.deleteById(id);
        } catch (Exception e) {
            throw new RuntimeException("Error deleting product with ID: " + id, e);
        }
    }

    public Page<ProductResponse> filterProducts(ProductFilterRequest filterRequest) {
        Specification<Product> spec =
                ProductSpecification.isNotDeleted()
                        .and(ProductSpecification.hasSubcategoryIds(filterRequest.getSubcategoryIds()))
                        .and(ProductSpecification.hasBrandIds(filterRequest.getBrandIds()))
                        .and(ProductSpecification.hasPriceBetween(filterRequest.getMinPrice(), filterRequest.getMaxPrice()))
                        .and(ProductSpecification.hasStatusIds(filterRequest.getStatusIds()));

        // Sorting
        String sortBy = filterRequest.getSortBy() != null ? filterRequest.getSortBy() : "name";
        String direction = filterRequest.getSortDirection() != null ? filterRequest.getSortDirection() : "asc";
        Sort sort = direction.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(filterRequest.getPage(), filterRequest.getSize(), sort);

        Page<Product> productPage = productRepository.findAll(spec, pageable);

        return productPage.map(converter::convertToProductResponse);
    }

}