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
                              LaptopSpecRepository laptopSpecRepository) {
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
        // Создаем основной продукт
        Product product = Product.builder()
                .name(dto.getName())
                .price(dto.getPrice())
                .priceDescription(dto.getPriceDescription())
                .title(dto.getTitle())
                .description(dto.getDescription())
                .availability(dto.isAvailability())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        // Устанавливаем подкатегорию
        if (dto.getSubCategoryId() != null) {
            product.setSubcategory(subcategoryRepository.findById(dto.getSubCategoryId())
                    .orElseThrow(() -> new RuntimeException("Subcategory not found")));
        }

        // Устанавливаем бренд
        if (dto.getBrandId() != null) {
            product.setBrand(brandRepository.findById(dto.getBrandId())
                    .orElseThrow(() -> new RuntimeException("Brand not found")));
        }

        Product savedProduct = productRepository.save(product);

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
        }

        // Обрабатываем цвета и их спецификации
        if (dto.getColors() != null && !dto.getColors().isEmpty()) {
            for (ColorRequest colorDto : dto.getColors()) {
                ProductColor color = new ProductColor();
                color.setName(colorDto.getName());
                color.setHex(colorDto.getHex());
                color.setProduct(savedProduct);
                ProductColor savedColor = productColorRepository.save(color);

                // Добавляем изображения для цвета
                if (colorDto.getImages() != null && !colorDto.getImages().isEmpty()) {
                    for (MultipartFile imageFile : colorDto.getImages()) {
                        FileEntity fileEntity = fileUploadService.saveImage(imageFile);
                        ProductColorImage colorImage = new ProductColorImage();
                        colorImage.setColor(savedColor);
                        colorImage.setImage(fileEntity);
                        productColorImageRepository.save(colorImage);
                    }
                }

                if (colorDto.getPhoneSpecs() != null && !colorDto.getPhoneSpecs().isEmpty()) {
                    for (PhoneSpecRequest phoneSpecDto : colorDto.getPhoneSpecs()) {
                        PhoneSpec phoneSpec = PhoneSpec.builder()
                                .productColor(savedColor)
                                .memory(phoneSpecDto.getMemory())
                                .price(phoneSpecDto.getPrice())
                                .simType(phoneSpecDto.getSimType())
                                .build();
                        phoneSpecRepository.save(phoneSpec);
                    }
                }

                if (colorDto.getLaptopSpecs() != null && !colorDto.getLaptopSpecs().isEmpty()) {
                    for (LaptopSpecRequest laptopSpecDto : colorDto.getLaptopSpecs()) {
                        LaptopSpec laptopSpec = LaptopSpec.builder()
                                .productColor(savedColor)
                                .ssdMemory(laptopSpecDto.getSsdMemory())
                                .price(laptopSpecDto.getPrice())
                                .build();
                        laptopSpecRepository.save(laptopSpec);
                    }
                }
            }
        }
        // Если нет цветов, но есть общие картинки
        else if (dto.getImages() != null && !dto.getImages().isEmpty()) {
            for (MultipartFile imageFile : dto.getImages()) {
                FileEntity fileEntity = fileUploadService.saveImage(imageFile);
                ProductImage productImage = new ProductImage();
                productImage.setProduct(savedProduct);
                productImage.setImage(fileEntity);
                productImageRepository.save(productImage);
            }
        }

        // Обрабатываем параметры
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

        // Возвращаем созданный продукт
        return productRepository.findByIdWithImagesAndStatuses(savedProduct.getId())
                .orElseThrow(() -> new RuntimeException("Product not found after creation"));
    }

    @Transactional
    public Product editProduct(UUID id, ProductRequest dto) throws Exception {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        // Обновляем базовые поля
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

        // Обновляем цвета и их спецификации
        if (dto.getColors() != null && !dto.getColors().isEmpty()) {
            // Удаляем старые цвета и все связанные с ними данные
            List<ProductColor> existingColors = productColorRepository.findByProductId(product.getId());
            for (ProductColor existingColor : existingColors) {
                // Удаляем спецификации телефонов
                phoneSpecRepository.deleteByProductColorId(existingColor.getId());
                // Удаляем спецификации ноутбуков
                laptopSpecRepository.deleteByProductColorId(existingColor.getId());
                // Удаляем изображения цветов
                productColorImageRepository.deleteByColorId(existingColor.getId());
            }
            // Удаляем сами цвета
            productColorRepository.deleteByProductId(product.getId());

            // Создаем новые цвета
            for (ColorRequest colorDto : dto.getColors()) {
                ProductColor color = new ProductColor();
                color.setName(colorDto.getName());
                color.setHex(colorDto.getHex());
                color.setProduct(updatedProduct);
                ProductColor savedColor = productColorRepository.save(color);

                // Добавляем изображения для цвета
                if (colorDto.getImages() != null && !colorDto.getImages().isEmpty()) {
                    for (MultipartFile imageFile : colorDto.getImages()) {
                        FileEntity fileEntity = fileUploadService.saveImage(imageFile);
                        ProductColorImage colorImage = new ProductColorImage();
                        colorImage.setColor(savedColor);
                        colorImage.setImage(fileEntity);
                        productColorImageRepository.save(colorImage);
                    }
                }

                // Обрабатываем спецификации в зависимости от категории продукта
                String categoryName = product.getSubcategory().getCategory().getName().toLowerCase();

                if ("phone".equals(categoryName) || "smartphone".equals(categoryName)) {
                    // Создаем спецификации для телефонов
                    if (colorDto.getPhoneSpecs() != null && !colorDto.getPhoneSpecs().isEmpty()) {
                        for (PhoneSpecRequest phoneSpecDto : colorDto.getPhoneSpecs()) {
                            PhoneSpec phoneSpec = PhoneSpec.builder()
                                    .productColor(savedColor)
                                    .memory(phoneSpecDto.getMemory())
                                    .price(phoneSpecDto.getPrice())
                                    .simType(phoneSpecDto.getSimType())
                                    .build();
                            phoneSpecRepository.save(phoneSpec);
                        }
                    }
                } else if ("laptop".equals(categoryName) || "notebook".equals(categoryName)) {
                    // Создаем спецификации для ноутбуков
                    if (colorDto.getLaptopSpecs() != null && !colorDto.getLaptopSpecs().isEmpty()) {
                        for (LaptopSpecRequest laptopSpecDto : colorDto.getLaptopSpecs()) {
                            LaptopSpec laptopSpec = LaptopSpec.builder()
                                    .productColor(savedColor)
                                    .ssdMemory(laptopSpecDto.getSsdMemory())
                                    .price(laptopSpecDto.getPrice())
                                    .build();
                            laptopSpecRepository.save(laptopSpec);
                        }
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

        // Обновляем параметры (остается без изменений)
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

        // Возвращаем обновленный продукт
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