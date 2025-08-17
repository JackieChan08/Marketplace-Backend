package com.example.marketplace_backend.Service.Impl;

import com.example.marketplace_backend.DTO.Requests.models.*;
import com.example.marketplace_backend.DTO.Responses.models.*;
import com.example.marketplace_backend.Model.*;
import com.example.marketplace_backend.Model.Intermediate_objects.ProductColorImage;
import com.example.marketplace_backend.Model.Intermediate_objects.ProductImage;
import com.example.marketplace_backend.Model.Intermediate_objects.ProductStatuses;
import com.example.marketplace_backend.Model.Phone.PhoneConnection;
import com.example.marketplace_backend.Model.Phone.PhoneConnectionAndProductColor;
import com.example.marketplace_backend.Model.Phone.ProductMemory;
import com.example.marketplace_backend.Model.Phone.ProductMemoryAndProductColor;
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
    private final ProductMemoryRepository productMemoryRepository;
    private final PhoneConnectionRepository phoneConnectionRepository;
    private final ProductMemoryAndProductColorRepository productMemoryAndProductColorRepository;
    private final PhoneConnectionAndProductColorRepository phoneConnectionAndProductColorRepository;

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
                              ProductMemoryRepository productMemoryRepository,
                              PhoneConnectionRepository phoneConnectionRepository,
                              ProductImageRepository productImageRepository,
                              ProductMemoryAndProductColorRepository productMemoryAndProductColorRepository,
                              PhoneConnectionAndProductColorRepository phoneConnectionAndProductColorRepository) {
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
        this.productMemoryRepository = productMemoryRepository;
        this.productMemoryAndProductColorRepository = productMemoryAndProductColorRepository;
        this.phoneConnectionAndProductColorRepository = phoneConnectionAndProductColorRepository;
        this.phoneConnectionRepository = phoneConnectionRepository;
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

        // 1. Если есть цвета — логика как сейчас
        if (dto.getColors() != null && !dto.getColors().isEmpty()) {
            for (ColorRequest colorDto : dto.getColors()) {
                ProductColor color = new ProductColor();
                color.setName(colorDto.getName());
                color.setHex(colorDto.getHex());
                color.setProduct(savedProduct);
                ProductColor savedColor = productColorRepository.save(color);

                // Сохраняем изображения для цвета
                if (colorDto.getImages() != null) {
                    for (MultipartFile imageFile : colorDto.getImages()) {
                        FileEntity fileEntity = fileUploadService.saveImage(imageFile);
                        ProductColorImage colorImage = new ProductColorImage();
                        colorImage.setColor(savedColor);
                        colorImage.setImage(fileEntity);
                        productColorImageRepository.save(colorImage);
                    }
                }

                // Обрабатываем память для каждого цвета
                if (colorDto.getMemoryIds() != null && !colorDto.getMemoryIds().isEmpty()) {
                    for (UUID memoryId : colorDto.getMemoryIds()) {
                        ProductMemory memory = productMemoryRepository.findById(memoryId)
                                .orElseThrow(() -> new RuntimeException("Memory not found with ID: " + memoryId));

                        ProductMemoryAndProductColor memoryColor = ProductMemoryAndProductColor.builder()
                                .productColor(savedColor)
                                .productMemory(memory)
                                .build();
                        productMemoryAndProductColorRepository.save(memoryColor);
                    }
                }

                // Обрабатываем типы подключения для каждого цвета
                if (colorDto.getConnectionIds() != null && !colorDto.getConnectionIds().isEmpty()) {
                    for (UUID connectionId : colorDto.getConnectionIds()) {
                        PhoneConnection connection = phoneConnectionRepository.findById(connectionId)
                                .orElseThrow(() -> new RuntimeException("Connection not found with ID: " + connectionId));

                        PhoneConnectionAndProductColor connectionColor = PhoneConnectionAndProductColor.builder()
                                .productColor(savedColor)
                                .phoneConnection(connection)
                                .build();
                        phoneConnectionAndProductColorRepository.save(connectionColor);
                    }
                }
            }
        }
        // 2. Если цветов нет, но есть общие фото
        else if (dto.getImages() != null && !dto.getImages().isEmpty()) {
            for (MultipartFile imageFile : dto.getImages()) {
                FileEntity fileEntity = fileUploadService.saveImage(imageFile);

                ProductImage productImage = new ProductImage();
                productImage.setProduct(savedProduct);
                productImage.setImage(fileEntity);
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
            Statuses defaultStatus = statusRepository.findByNameByProductFlag("Без статуса")
                    .orElseThrow(() -> new RuntimeException("Default status not found"));

            ProductStatuses productStatus = ProductStatuses.builder()
                    .product(savedProduct)
                    .status(defaultStatus)
                    .build();
            productStatusRepository.save(productStatus);
        }

        // Обрабатываем параметры продукта
        ObjectMapper mapper = new ObjectMapper();
        List<ProductParameterRequest> parameters = new ArrayList<>();

        if (dto.getParametersJson() != null && !dto.getParametersJson().isEmpty()) {
            parameters = mapper.readValue(
                    dto.getParametersJson(),
                    new TypeReference<List<ProductParameterRequest>>() {}
            );
        }

        if (parameters != null && !parameters.isEmpty()) {
            for (ProductParameterRequest paramReq : parameters) {
                ProductParameters parameter = new ProductParameters();
                parameter.setName(paramReq.getName());
                parameter.setProduct(savedProduct); // исправлено: было product, должно быть savedProduct

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

        return savedProduct;
    }

    @Transactional
    public Product editProduct(UUID id, ProductRequest dto) throws Exception {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        // Обновляем базовые поля
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

        // Обновляем статусы
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

        // Обновляем цвета
        if (dto.getColors() != null && !dto.getColors().isEmpty()) {
            // Удаляем старые цвета, картинки и память
            productColorImageRepository.deleteByProductId(product.getId());
            productColorRepository.deleteByProductId(product.getId());

            // Создаем новые цвета
            for (ColorRequest colorDto : dto.getColors()) {
                ProductColor color = new ProductColor();
                color.setName(colorDto.getName());
                color.setHex(colorDto.getHex());
                color.setProduct(updatedProduct);
                ProductColor savedColor = productColorRepository.save(color);

                // Добавляем изображения
                if (colorDto.getImages() != null) {
                    for (MultipartFile imageFile : colorDto.getImages()) {
                        FileEntity fileEntity = fileUploadService.saveImage(imageFile);
                        ProductColorImage colorImage = new ProductColorImage();
                        colorImage.setColor(savedColor);
                        colorImage.setImage(fileEntity);
                        productColorImageRepository.save(colorImage);
                    }
                }
            }
        }
        // Если нет цветов, но есть общие картинки
        else if (dto.getImages() != null && !dto.getImages().isEmpty()) {
            productImageRepository.deleteByProductId(product.getId());
            for (MultipartFile imageFile : dto.getImages()) {
                FileEntity fileEntity = fileUploadService.saveImage(imageFile);
                ProductImage productImage = new ProductImage();
                productImage.setProduct(updatedProduct);
                productImage.setImage(fileEntity);
                productImageRepository.save(productImage);
            }
        }

        // Обновляем параметры
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

        // Возвращаем обновленный продукт с join fetch всех связанных сущностей
        return productRepository.findByIdWithImagesAndStatuses(updatedProduct.getId())
                .orElseThrow(() -> new RuntimeException("Product not found after update"));
    }


    @Override
    public void delete(UUID id) {
        try {
            productRepository.deleteById(id);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при удалении продукта с ID: " + id, e);
        }
    }

//    public boolean deleteProductImage(UUID productId, UUID imageId) {
//        Optional<Product> productOpt = productRepository.findById(productId);
//
//        if (productOpt.isEmpty()) {
//            return false;
//        }
//
//        Product product = productOpt.get();
//
//        boolean removed = false;
//        if(product.getProductColorImages() != null) {
//            removed = product.getProductColorImages().removeIf(img ->
//                    img.getImage() != null && img.getProduct().getId().equals(productId));
//        }
//
//        if (removed) {
//            productRepository.save(product);
//        }
//
//        return removed;
//    }

    public Page<ProductResponse> filterProducts(ProductFilterRequest filterRequest) {
        Specification<Product> spec =
                ProductSpecification.isNotDeleted()
                        .and(ProductSpecification.hasSubcategoryIds(filterRequest.getSubcategoryIds()))
                        .and(ProductSpecification.hasBrandIds(filterRequest.getBrandIds()))
                        .and(ProductSpecification.hasPriceBetween(filterRequest.getMinPrice(), filterRequest.getMaxPrice()))
                        .and(ProductSpecification.hasStatusIds(filterRequest.getStatusIds()));


        // Сортировка
        String sortBy = filterRequest.getSortBy() != null ? filterRequest.getSortBy() : "name";
        String direction = filterRequest.getSortDirection() != null ? filterRequest.getSortDirection() : "asc";
        Sort sort = direction.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(filterRequest.getPage(), filterRequest.getSize(), sort);

        Page<Product> productPage = productRepository.findAll(spec, pageable);

        return productPage.map(converter::convertToProductResponse);
    }
}