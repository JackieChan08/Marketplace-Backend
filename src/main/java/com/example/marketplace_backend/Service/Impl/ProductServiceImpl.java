package com.example.marketplace_backend.Service.Impl;

import com.example.marketplace_backend.DTO.Requests.models.*;
import com.example.marketplace_backend.DTO.Requests.models.LaptopRequest.*;
import com.example.marketplace_backend.DTO.Requests.models.PhoneSpecRequest.PhoneMemoryRequest;
import com.example.marketplace_backend.DTO.Requests.models.PhoneSpecRequest.SimTypeRequest;
import com.example.marketplace_backend.DTO.Requests.models.TableRequest.TableMemoryRequest;
import com.example.marketplace_backend.DTO.Requests.models.TableRequest.TableModuleRequest;
import com.example.marketplace_backend.DTO.Requests.models.WatchRequest.DialRequest;
import com.example.marketplace_backend.DTO.Requests.models.WatchRequest.StrapSizeRequest;
import com.example.marketplace_backend.DTO.Responses.models.*;
import com.example.marketplace_backend.Model.*;
import com.example.marketplace_backend.Model.Intermediate_objects.ProductColorImage;
import com.example.marketplace_backend.Model.Intermediate_objects.ProductImage;
import com.example.marketplace_backend.Model.Intermediate_objects.ProductStatuses;
import com.example.marketplace_backend.Model.ProductSpec.LaptopSpec.*;
import com.example.marketplace_backend.Model.ProductSpec.PhoneSpec.PhoneMemory;
import com.example.marketplace_backend.Model.ProductSpec.PhoneSpec.PhoneSpec;
import com.example.marketplace_backend.Model.ProductSpec.PhoneSpec.SimType;
import com.example.marketplace_backend.Model.ProductSpec.TableSpec.TableMemory;
import com.example.marketplace_backend.Model.ProductSpec.TableSpec.TableModule;
import com.example.marketplace_backend.Model.ProductSpec.TableSpec.TableSpec;
import com.example.marketplace_backend.Model.ProductSpec.WatchSpec.Dial;
import com.example.marketplace_backend.Model.ProductSpec.WatchSpec.StrapSize;
import com.example.marketplace_backend.Model.ProductSpec.WatchSpec.WatchSpec;
import com.example.marketplace_backend.Repositories.*;
import com.example.marketplace_backend.Repositories.LaptopRepository.ChipRepository;
import com.example.marketplace_backend.Repositories.LaptopRepository.LaptopSpecRepository;
import com.example.marketplace_backend.Repositories.LaptopRepository.RamRepository;
import com.example.marketplace_backend.Repositories.LaptopRepository.SsdRepository;
import com.example.marketplace_backend.Repositories.PhoneSpec.PhoneMemoryRepository;
import com.example.marketplace_backend.Repositories.PhoneSpec.PhoneSpecRepository;
import com.example.marketplace_backend.Repositories.PhoneSpec.SimTypeRepository;
import com.example.marketplace_backend.Repositories.TableRepository.TableMemoryRepository;
import com.example.marketplace_backend.Repositories.TableRepository.TableModuleRepository;
import com.example.marketplace_backend.Repositories.TableRepository.TableSpecRepository;
import com.example.marketplace_backend.Repositories.WatchRepository.DialRepository;
import com.example.marketplace_backend.Repositories.WatchRepository.StrapSizeRepository;
import com.example.marketplace_backend.Repositories.WatchRepository.WatchSpecRepository;
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

import java.math.BigDecimal;
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
    private final ProductVariantRepository productVariantRepository;
    private final PhoneSpecRepository phoneSpecRepository;
    private final SimTypeRepository simTypeRepository;
    private final PhoneMemoryRepository phoneMemoryRepository;
    private final LaptopSpecRepository laptopSpecRepository;
    private final ChipRepository chipRepository;
    private final SsdRepository ssdRepository;
    private final RamRepository ramRepository;
    private final WatchSpecRepository watchSpecRepository;
    private final StrapSizeRepository strapSizeRepository;
    private final DialRepository dialRepository;
    private final TableSpecRepository tableSpecRepository;
    private final TableModuleRepository tableModuleRepository;
    private final TableMemoryRepository tableMemoryRepository;
    private final FileRepository fileRepository;

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
                              ProductVariantRepository productVariantRepository,
                              PhoneSpecRepository phoneSpecRepository,
                              SimTypeRepository simTypeRepository,
                              PhoneMemoryRepository phoneMemoryRepository,
                              LaptopSpecRepository laptopSpecRepository,
                              ChipRepository chipRepository,
                              SsdRepository ssdRepository,
                              RamRepository ramRepository,
                              WatchSpecRepository watchSpecRepository,
                              StrapSizeRepository strapSizeRepository,
                              DialRepository dialRepository,
                              TableSpecRepository tableSpecRepository,
                              TableModuleRepository tableModuleRepository,
                              TableMemoryRepository tableMemoryRepository, FileRepository fileRepository) {
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
        this.productVariantRepository = productVariantRepository;
        this.phoneSpecRepository = phoneSpecRepository;
        this.simTypeRepository = simTypeRepository;
        this.phoneMemoryRepository = phoneMemoryRepository;
        this.laptopSpecRepository = laptopSpecRepository;
        this.chipRepository = chipRepository;
        this.ssdRepository = ssdRepository;
        this.ramRepository = ramRepository;
        this.watchSpecRepository = watchSpecRepository;
        this.strapSizeRepository = strapSizeRepository;
        this.dialRepository = dialRepository;
        this.tableSpecRepository = tableSpecRepository;
        this.tableModuleRepository = tableModuleRepository;
        this.tableMemoryRepository = tableMemoryRepository;
        this.fileRepository = fileRepository;
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
        // Создание базового продукта
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

        // Подкатегория
        if (request.getSubCategoryId() != null) {
            product.setSubcategory(subcategoryRepository.findById(request.getSubCategoryId())
                    .orElseThrow(() -> new RuntimeException("Subcategory not found")));
        }

        // Бренд
        if (request.getBrandId() != null) {
            product.setBrand(brandRepository.findById(request.getBrandId())
                    .orElseThrow(() -> new RuntimeException("Brand not found")));
        }

        Product savedProduct = productRepository.save(product);

        // Статусы
        if (request.getStatusId() != null && !request.getStatusId().isEmpty()) {
            for (UUID statusId : request.getStatusId()) {
                Statuses status = statusRepository.findById(statusId)
                        .orElseThrow(() -> new RuntimeException("Status not found with ID: " + statusId));
                ProductStatuses ps = ProductStatuses.builder()
                        .product(savedProduct)
                        .status(status)
                        .build();
                productStatusRepository.save(ps);
            }
        }

        // Переменная для хранения первой найденной цены из спецификаций
        BigDecimal firstSpecPrice = null;

        // Обработка цветов и спецификаций
        if (request.getColors() != null && !request.getColors().isEmpty()) {
            for (ColorWithSpecsRequest colorReq : request.getColors()) {
                // Проверка, чтобы одновременно не было нескольких типов спецификаций
                int specCount = 0;
                if (colorReq.getSimTypeRequests() != null && !colorReq.getSimTypeRequests().isEmpty()) specCount++;
                if (colorReq.getChipRequests() != null && !colorReq.getChipRequests().isEmpty()) specCount++;
                if (colorReq.getStrapSizeRequests() != null && !colorReq.getStrapSizeRequests().isEmpty()) specCount++;
                if (colorReq.getTableModuleRequests() != null && !colorReq.getTableModuleRequests().isEmpty()) specCount++;

                if (specCount > 1) {
                    throw new RuntimeException("Color " + colorReq.getName() + " can only have one type of specification");
                }

                // Создаём цвет
                ProductColor color = ProductColor.builder()
                        .name(colorReq.getName())
                        .hex(colorReq.getHex())
                        .price(colorReq.getPrice())
                        .build();
                ProductColor savedColor = productColorRepository.save(color);

                // Сохраняем изображения цвета
                if (colorReq.getImages() != null) {
                    for (MultipartFile imageFile : colorReq.getImages()) {
                        FileEntity fileEntity = fileUploadService.saveImage(imageFile);
                        ProductColorImage colorImage = ProductColorImage.builder()
                                .color(savedColor)
                                .image(fileEntity)
                                .build();
                        productColorImageRepository.save(colorImage);
                    }
                }

                // СПЕЦИФИКАЦИИ ТЕЛЕФОНОВ
                if (colorReq.getSimTypeRequests() != null && !colorReq.getSimTypeRequests().isEmpty()) {
                    for (SimTypeRequest simTypeReq : colorReq.getSimTypeRequests()) {
                        SimType simType = SimType.builder()
                                .name(simTypeReq.getName())
                                .build();
                        SimType savedSimType = simTypeRepository.save(simType);

                        if (simTypeReq.getPhoneMemoryRequests() != null && !simTypeReq.getPhoneMemoryRequests().isEmpty()) {
                            for (PhoneMemoryRequest memoryReq : simTypeReq.getPhoneMemoryRequests()) {
                                // Сохраняем первую цену, если еще не установлена
                                if (firstSpecPrice == null && memoryReq.getPrice() != null) {
                                    firstSpecPrice = memoryReq.getPrice();
                                }

                                PhoneMemory phoneMemory = PhoneMemory.builder()
                                        .name(memoryReq.getName())
                                        .price(memoryReq.getPrice())
                                        .build();
                                PhoneMemory savedPhoneMemory = phoneMemoryRepository.save(phoneMemory);

                                PhoneSpec phoneSpec = PhoneSpec.builder()
                                        .simType(savedSimType)
                                        .phoneMemory(savedPhoneMemory)
                                        .build();
                                PhoneSpec savedPhoneSpec = phoneSpecRepository.save(phoneSpec);

                                ProductVariant variant = ProductVariant.builder()
                                        .product(savedProduct)
                                        .color(savedColor)
                                        .phoneSpec(savedPhoneSpec)
                                        .price(memoryReq.getPrice())
                                        .build();
                                productVariantRepository.save(variant);
                            }
                        }
                    }
                }
                // Спецификации для ноутбука
                else if (colorReq.getChipRequests() != null && !colorReq.getChipRequests().isEmpty()) {
                    for (ChipRequest chipReq : colorReq.getChipRequests()) {
                        Chip chip = Chip.builder()
                                .name(chipReq.getName())
                                .build();
                        Chip savedChip = chipRepository.save(chip);

                        if (chipReq.getSsdRequests() != null && !chipReq.getSsdRequests().isEmpty()) {
                            for (SsdRequest ssdReq : chipReq.getSsdRequests()) {
                                Ssd ssd = Ssd.builder()
                                        .name(ssdReq.getName())
                                        .build();
                                Ssd savedSsd = ssdRepository.save(ssd);

                                if (ssdReq.getRamRequests() != null && !ssdReq.getRamRequests().isEmpty()) {
                                    for (RamRequest ramReq : ssdReq.getRamRequests()) {
                                        // Сохраняем первую цену, если еще не установлена
                                        if (firstSpecPrice == null && ramReq.getPrice() != null) {
                                            firstSpecPrice = ramReq.getPrice();
                                        }

                                        Ram ram = Ram.builder()
                                                .name(ramReq.getName())
                                                .price(ramReq.getPrice())
                                                .build();
                                        Ram savedRam = ramRepository.save(ram);

                                        LaptopSpec laptopSpec = LaptopSpec.builder()
                                                .chip(savedChip)
                                                .ssd(savedSsd)
                                                .ram(savedRam)
                                                .build();
                                        LaptopSpec savedLaptopSpec = laptopSpecRepository.save(laptopSpec);

                                        ProductVariant variant = ProductVariant.builder()
                                                .product(savedProduct)
                                                .color(savedColor)
                                                .laptopSpec(savedLaptopSpec)
                                                .price(ramReq.getPrice())
                                                .build();
                                        productVariantRepository.save(variant);
                                    }
                                }
                            }
                        }
                    }
                }
                // СПЕЦИФИКАЦИИ ЧАСОВ
                else if (colorReq.getStrapSizeRequests() != null && !colorReq.getStrapSizeRequests().isEmpty()) {
                    for (StrapSizeRequest strapSizeReq : colorReq.getStrapSizeRequests()) {
                        StrapSize strapSize = StrapSize.builder()
                                .name(strapSizeReq.getName())
                                .build();
                        StrapSize savedStrapSize = strapSizeRepository.save(strapSize);

                        if (strapSizeReq.getDialRequests() != null && !strapSizeReq.getDialRequests().isEmpty()) {
                            for (DialRequest dialReq : strapSizeReq.getDialRequests()) {
                                // Сохраняем первую цену, если еще не установлена
                                if (firstSpecPrice == null && dialReq.getPrice() != null) {
                                    firstSpecPrice = dialReq.getPrice();
                                }

                                Dial dial = Dial.builder()
                                        .name(dialReq.getSize_mm())
                                        .price(dialReq.getPrice())
                                        .build();
                                Dial savedDial = dialRepository.save(dial);

                                WatchSpec watchSpec = WatchSpec.builder()
                                        .strapSize(savedStrapSize)
                                        .dial(savedDial)
                                        .build();
                                WatchSpec savedWatchSpec = watchSpecRepository.save(watchSpec);

                                ProductVariant variant = ProductVariant.builder()
                                        .product(savedProduct)
                                        .color(savedColor)
                                        .watchSpec(savedWatchSpec)
                                        .price(dialReq.getPrice())
                                        .build();
                                productVariantRepository.save(variant);
                            }
                        } else {
                            // Нет dials — создаём базовый dial
                            Dial dial = Dial.builder()
                                    .name("Standard")
                                    .price(BigDecimal.ZERO)
                                    .build();
                            Dial savedDial = dialRepository.save(dial);

                            WatchSpec watchSpec = WatchSpec.builder()
                                    .strapSize(savedStrapSize)
                                    .dial(savedDial)
                                    .build();
                            WatchSpec savedWatchSpec = watchSpecRepository.save(watchSpec);

                            ProductVariant variant = ProductVariant.builder()
                                    .product(savedProduct)
                                    .color(savedColor)
                                    .watchSpec(savedWatchSpec)
                                    .build();
                            productVariantRepository.save(variant);
                        }
                    }
                }
                // СПЕЦИФИКАЦИИ ПЛАНШЕТОВ
                else if (colorReq.getTableModuleRequests() != null && !colorReq.getTableModuleRequests().isEmpty()) {
                    for (TableModuleRequest moduleReq : colorReq.getTableModuleRequests()) {
                        TableModule tableModule = TableModule.builder()
                                .name(moduleReq.getName())
                                .build();
                        TableModule savedModule = tableModuleRepository.save(tableModule);

                        if (moduleReq.getTableMemoryRequests() != null && !moduleReq.getTableMemoryRequests().isEmpty()) {
                            for (TableMemoryRequest memoryReq : moduleReq.getTableMemoryRequests()) {
                                // Сохраняем первую цену, если еще не установлена
                                if (firstSpecPrice == null && memoryReq.getPrice() != null) {
                                    firstSpecPrice = memoryReq.getPrice();
                                }

                                TableMemory tableMemory = TableMemory.builder()
                                        .name(memoryReq.getName())
                                        .price(memoryReq.getPrice())
                                        .build();
                                TableMemory savedMemory = tableMemoryRepository.save(tableMemory);

                                TableSpec tableSpec = TableSpec.builder()
                                        .tableModule(savedModule)
                                        .tableMemory(savedMemory)
                                        .build();
                                TableSpec savedTableSpec = tableSpecRepository.save(tableSpec);

                                ProductVariant variant = ProductVariant.builder()
                                        .product(savedProduct)
                                        .color(savedColor)
                                        .tableSpec(savedTableSpec)
                                        .price(memoryReq.getPrice())
                                        .build();
                                productVariantRepository.save(variant);
                            }
                        } else {
                            TableMemory tableMemory = TableMemory.builder()
                                    .name("Standard")
                                    .price(BigDecimal.ZERO)
                                    .build();
                            TableMemory savedMemory = tableMemoryRepository.save(tableMemory);

                            TableSpec tableSpec = TableSpec.builder()
                                    .tableModule(savedModule)
                                    .tableMemory(savedMemory)
                                    .build();
                            TableSpec savedTableSpec = tableSpecRepository.save(tableSpec);

                            ProductVariant variant = ProductVariant.builder()
                                    .product(savedProduct)
                                    .color(savedColor)
                                    .tableSpec(savedTableSpec)
                                    .build();
                            productVariantRepository.save(variant);
                        }
                    }
                }
                // Если нет спецификаций — создаём вариант только с цветом
                else {
                    ProductVariant variant = ProductVariant.builder()
                            .product(savedProduct)
                            .color(savedColor)
                            .build();
                    productVariantRepository.save(variant);
                }
            }
        } else if (request.getImages() != null && !request.getImages().isEmpty()) {
            // Общие изображения продукта без вариантов цвета
            for (MultipartFile imageFile : request.getImages()) {
                FileEntity fileEntity = fileUploadService.saveImage(imageFile);
                ProductImage productImage = ProductImage.builder()
                        .product(savedProduct)
                        .image(fileEntity)
                        .build();
                productImageRepository.save(productImage);

                ProductVariant variant = ProductVariant.builder()
                        .product(savedProduct)
                        .build();
                productVariantRepository.save(variant);
            }
        }

        // Устанавливаем цену продукта из первой спецификации, если она не была указана
        if (request.getPrice() == null && firstSpecPrice != null) {
            savedProduct.setPrice(firstSpecPrice);
            productRepository.save(savedProduct);
        }

        // Параметры продукта
        ObjectMapper mapper = new ObjectMapper();
        List<ProductParameterRequest> parameters = new ArrayList<>();
        if (request.getParametersJson() != null && !request.getParametersJson().isEmpty()) {
            parameters = mapper.readValue(
                    request.getParametersJson(),
                    new TypeReference<List<ProductParameterRequest>>() {}
            );
        }

        if (!parameters.isEmpty()) {
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
        return savedProduct;
    }

    @Transactional
    public Product editProduct(UUID productId, ProductRequest request) throws Exception {
        // Находим существующий продукт
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        // Обновляем основные поля
        product.setName(request.getName());
        product.setPrice(request.getPrice());
        product.setPriceDescription(request.getPriceDescription());
        product.setTitle(request.getTitle());
        product.setDescription(request.getDescription());
        product.setAvailability(request.isAvailability());
        product.setUpdatedAt(LocalDateTime.now());

        // Подкатегория
        if (request.getSubCategoryId() != null) {
            product.setSubcategory(subcategoryRepository.findById(request.getSubCategoryId())
                    .orElseThrow(() -> new RuntimeException("Subcategory not found")));
        } else {
            product.setSubcategory(null);
        }

        // Бренд
        if (request.getBrandId() != null) {
            product.setBrand(brandRepository.findById(request.getBrandId())
                    .orElseThrow(() -> new RuntimeException("Brand not found")));
        } else {
            product.setBrand(null);
        }

        // --- ЧИСТИМ СТАРЫЕ СВЯЗИ ---
        productVariantRepository.deleteByProductId(productId);
        productStatusRepository.deleteByProductId(productId);
        productImageRepository.deleteByProductId(productId);
        productColorRepository.deleteByProductId(productId);
        productParametersRepository.deleteByProductId(productId);

        Product savedProduct = productRepository.save(product);

        // --- ДАЛЬШЕ ПОВТОРЯЕМ ЛОГИКУ СОЗДАНИЯ ---
        // Статусы
        if (request.getStatusId() != null && !request.getStatusId().isEmpty()) {
            for (UUID statusId : request.getStatusId()) {
                Statuses status = statusRepository.findById(statusId)
                        .orElseThrow(() -> new RuntimeException("Status not found with ID: " + statusId));
                ProductStatuses ps = ProductStatuses.builder()
                        .product(savedProduct)
                        .status(status)
                        .build();
                productStatusRepository.save(ps);
            }
        }

        //Цвет и спецификации
        if (request.getColors() != null && !request.getColors().isEmpty()) {
            for (ColorWithSpecsRequest colorReq : request.getColors()) {
                // Проверка, чтобы одновременно не было нескольких типов спецификаций
                int specCount = 0;
                if (colorReq.getSimTypeRequests() != null && !colorReq.getSimTypeRequests().isEmpty()) specCount++;
                if (colorReq.getChipRequests() != null && !colorReq.getChipRequests().isEmpty()) specCount++;
                if (colorReq.getStrapSizeRequests() != null && !colorReq.getStrapSizeRequests().isEmpty()) specCount++;
                if (colorReq.getTableModuleRequests() != null && !colorReq.getTableModuleRequests().isEmpty()) specCount++;

                if (specCount > 1) {
                    throw new RuntimeException("Color " + colorReq.getName() + " can only have one type of specification");
                }

                // Создаём цвет
                ProductColor color = ProductColor.builder()
                        .name(colorReq.getName())
                        .hex(colorReq.getHex())
                        .price(colorReq.getPrice())
                        .build();
                ProductColor savedColor = productColorRepository.save(color);

                // Сохраняем изображения цвета
                if (colorReq.getImages() != null) {
                    for (MultipartFile imageFile : colorReq.getImages()) {
                        FileEntity fileEntity = fileUploadService.saveImage(imageFile);
                        ProductColorImage colorImage = ProductColorImage.builder()
                                .color(savedColor)
                                .image(fileEntity)
                                .build();
                        productColorImageRepository.save(colorImage);
                    }
                }

                // ИСПРАВЛЕННЫЕ СПЕЦИФИКАЦИИ ТЕЛЕФОНОВ
                if (colorReq.getSimTypeRequests() != null && !colorReq.getSimTypeRequests().isEmpty()) {
                    // Обрабатываем структуру SimType → PhoneMemory
                    for (SimTypeRequest simTypeReq : colorReq.getSimTypeRequests()) {
                        // Создаем или находим SimType
                        SimType simType = SimType.builder()
                                .name(simTypeReq.getName())
                                .build();
                        SimType savedSimType = simTypeRepository.save(simType);

                        if (simTypeReq.getPhoneMemoryRequests() != null && !simTypeReq.getPhoneMemoryRequests().isEmpty()) {
                            for (PhoneMemoryRequest memoryReq : simTypeReq.getPhoneMemoryRequests()) {
                                // Создаем PhoneMemory
                                PhoneMemory phoneMemory = PhoneMemory.builder()
                                        .name(memoryReq.getName())
                                        .price(memoryReq.getPrice())
                                        .build();
                                PhoneMemory savedPhoneMemory = phoneMemoryRepository.save(phoneMemory);

                                // Создаем PhoneSpec с правильными связями
                                PhoneSpec phoneSpec = PhoneSpec.builder()
                                        .simType(savedSimType)
                                        .phoneMemory(savedPhoneMemory)
                                        .build();
                                PhoneSpec savedPhoneSpec = phoneSpecRepository.save(phoneSpec);

                                ProductVariant variant = ProductVariant.builder()
                                        .product(savedProduct)
                                        .color(savedColor)
                                        .phoneSpec(savedPhoneSpec)
                                        .build();
                                productVariantRepository.save(variant);
                            }
                        }
                    }
                }
                // Спецификации для ноутбука (уже правильно реализованы)
                else if (colorReq.getChipRequests() != null && !colorReq.getChipRequests().isEmpty()) {
                    for (ChipRequest chipReq : colorReq.getChipRequests()) {
                        Chip chip = Chip.builder()
                                .name(chipReq.getName())
                                .build();
                        Chip savedChip = chipRepository.save(chip);

                        // Обработка SSD
                        if (chipReq.getSsdRequests() != null && !chipReq.getSsdRequests().isEmpty()) {
                            for (SsdRequest ssdReq : chipReq.getSsdRequests()) {
                                Ssd ssd = Ssd.builder()
                                        .name(ssdReq.getName())
                                        .build();
                                Ssd savedSsd = ssdRepository.save(ssd);

                                // Обработка RAM
                                if (ssdReq.getRamRequests() != null && !ssdReq.getRamRequests().isEmpty()) {
                                    for (RamRequest ramReq : ssdReq.getRamRequests()) {
                                        Ram ram = Ram.builder()
                                                .name(ramReq.getName())
                                                .price(ramReq.getPrice())
                                                .build();
                                        Ram savedRam = ramRepository.save(ram);

                                        // Каждая комбинация chip + ssd + ram = LaptopSpec
                                        LaptopSpec laptopSpec = LaptopSpec.builder()
                                                .chip(savedChip)
                                                .ssd(savedSsd)
                                                .ram(savedRam)
                                                .build();
                                        LaptopSpec savedLaptopSpec = laptopSpecRepository.save(laptopSpec);

                                        // Привязываем ProductVariant
                                        ProductVariant variant = ProductVariant.builder()
                                                .product(savedProduct)
                                                .color(savedColor)
                                                .laptopSpec(savedLaptopSpec)
                                                .build();
                                        productVariantRepository.save(variant);
                                    }
                                }
                            }
                        }
                    }
                }
                // ИСПРАВЛЕННЫЕ СПЕЦИФИКАЦИИ ЧАСОВ
                else if (colorReq.getStrapSizeRequests() != null && !colorReq.getStrapSizeRequests().isEmpty()) {
                    // Обрабатываем структуру StrapSize → Dial
                    for (StrapSizeRequest strapSizeReq : colorReq.getStrapSizeRequests()) {
                        // Создаем StrapSize
                        StrapSize strapSize = StrapSize.builder()
                                .name(strapSizeReq.getName())
                                .build();
                        StrapSize savedStrapSize = strapSizeRepository.save(strapSize);

                        // Если есть dials — развертываем комбинации strapSize × dial
                        if (strapSizeReq.getDialRequests() != null && !strapSizeReq.getDialRequests().isEmpty()) {
                            for (DialRequest dialReq : strapSizeReq.getDialRequests()) {
                                // Создаем Dial
                                Dial dial = Dial.builder()
                                        .name(dialReq.getSize_mm()) // используем size_mm как name
                                        .price(dialReq.getPrice())
                                        .build();
                                Dial savedDial = dialRepository.save(dial);

                                // Создаем WatchSpec с правильными связями
                                WatchSpec watchSpec = WatchSpec.builder()
                                        .strapSize(savedStrapSize)
                                        .dial(savedDial)
                                        .build();
                                WatchSpec savedWatchSpec = watchSpecRepository.save(watchSpec);

                                ProductVariant variant = ProductVariant.builder()
                                        .product(savedProduct)
                                        .color(savedColor)
                                        .watchSpec(savedWatchSpec)
                                        .build();
                                productVariantRepository.save(variant);
                            }
                        } else {
                            // Нет dials — создаём базовый dial
                            Dial dial = Dial.builder()
                                    .name("Standard")
                                    .price(BigDecimal.ZERO)
                                    .build();
                            Dial savedDial = dialRepository.save(dial);

                            WatchSpec watchSpec = WatchSpec.builder()
                                    .strapSize(savedStrapSize)
                                    .dial(savedDial)
                                    .build();
                            WatchSpec savedWatchSpec = watchSpecRepository.save(watchSpec);

                            ProductVariant variant = ProductVariant.builder()
                                    .product(savedProduct)
                                    .color(savedColor)
                                    .watchSpec(savedWatchSpec)
                                    .build();
                            productVariantRepository.save(variant);
                        }
                    }
                }
                // ИСПРАВЛЕННЫЕ СПЕЦИФИКАЦИИ ПЛАНШЕТОВ
                else if (colorReq.getTableModuleRequests() != null && !colorReq.getTableModuleRequests().isEmpty()) {
                    // Обрабатываем структуру TableModule → TableMemory
                    for (TableModuleRequest moduleReq : colorReq.getTableModuleRequests()) {
                        // Создаем TableModule
                        TableModule tableModule = TableModule.builder()
                                .name(moduleReq.getName())
                                .build();
                        TableModule savedModule = tableModuleRepository.save(tableModule);

                        if (moduleReq.getTableMemoryRequests() != null && !moduleReq.getTableMemoryRequests().isEmpty()) {
                            for (TableMemoryRequest memoryReq : moduleReq.getTableMemoryRequests()) {
                                // Создаем TableMemory
                                TableMemory tableMemory = TableMemory.builder()
                                        .name(memoryReq.getName())
                                        .price(memoryReq.getPrice())
                                        .build();
                                TableMemory savedMemory = tableMemoryRepository.save(tableMemory);

                                // Создаем TableSpec с правильными связями
                                TableSpec tableSpec = TableSpec.builder()
                                        .tableModule(savedModule)
                                        .tableMemory(savedMemory)
                                        .build();
                                TableSpec savedTableSpec = tableSpecRepository.save(tableSpec);

                                // Создаем ProductVariant для каждой комбинации
                                ProductVariant variant = ProductVariant.builder()
                                        .product(savedProduct)
                                        .color(savedColor)
                                        .tableSpec(savedTableSpec)
                                        .build();
                                productVariantRepository.save(variant);
                            }
                        } else {
                            // Если нет памяти, создаем базовую память
                            TableMemory tableMemory = TableMemory.builder()
                                    .name("Standard")
                                    .price(BigDecimal.ZERO)
                                    .build();
                            TableMemory savedMemory = tableMemoryRepository.save(tableMemory);

                            TableSpec tableSpec = TableSpec.builder()
                                    .tableModule(savedModule)
                                    .tableMemory(savedMemory)
                                    .build();
                            TableSpec savedTableSpec = tableSpecRepository.save(tableSpec);

                            ProductVariant variant = ProductVariant.builder()
                                    .product(savedProduct)
                                    .color(savedColor)
                                    .tableSpec(savedTableSpec)
                                    .build();
                            productVariantRepository.save(variant);
                        }
                    }
                }
                // Если нет спецификаций — создаём вариант только с цветом
                else {
                    ProductVariant variant = ProductVariant.builder()
                            .product(savedProduct)
                            .color(savedColor)
                            .build();
                    productVariantRepository.save(variant);
                }
            }
        } else if (request.getImages() != null && !request.getImages().isEmpty()) {
            for (MultipartFile imageFile : request.getImages()) {
                FileEntity fileEntity = fileUploadService.saveImage(imageFile);
                ProductImage productImage = ProductImage.builder()
                        .product(savedProduct)
                        .image(fileEntity)
                        .build();
                productImageRepository.save(productImage);
            }
        }

        // Параметры
        ObjectMapper mapper = new ObjectMapper();
        if (request.getParametersJson() != null && !request.getParametersJson().isEmpty()) {
            List<ProductParameterRequest> parameters = mapper.readValue(
                    request.getParametersJson(),
                    new TypeReference<List<ProductParameterRequest>>() {}
            );

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

        return savedProduct;
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

    @Transactional
    public ProductImage addProductImage(UUID productId, MultipartFile imageFile) throws Exception {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        FileEntity fileEntity = fileUploadService.saveImage(imageFile);

        ProductImage productImage = ProductImage.builder()
                .product(product)
                .image(fileEntity)
                .build();

        return productImageRepository.save(productImage);
    }

    @Transactional
    public void deleteProductImage(UUID imageId) {
        ProductImage image = productImageRepository.findById(imageId)
                .orElseThrow(() -> new RuntimeException("Image not found"));

        fileUploadService.deleteImage(image.getImage().getFilePath());

        productImageRepository.delete(image);
    }

    @Transactional
    public ProductColorImage addProductColorImage(UUID productColorId, MultipartFile imageFile) throws Exception {
        ProductColor productColor = productColorRepository.findById(productColorId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        FileEntity fileEntity = fileUploadService.saveImage(imageFile);

        ProductColorImage productColorImage = ProductColorImage.builder()
                .color(productColor)
                .image(fileEntity)
                .build();

        return productColorImageRepository.save(productColorImage);
    }

    @Transactional
    public void deleteProductColorImage(UUID productColorImageId) {
        ProductColorImage pci = productColorImageRepository.findById(productColorImageId)
                .orElseThrow(() -> new RuntimeException("Image not found"));

        FileEntity file = pci.getImage();

        // Считаем, сколько ссылок на этот файл есть в БД
        long refs = productColorImageRepository.countByImageId(file.getId());

        // Удаляем только связь
        productColorImageRepository.delete(pci);

        // Если это была последняя ссылка — удаляем сам файл
        if (refs <= 1) {
            fileUploadService.deleteImage(file.getFilePath()); // удаляем физически
            fileRepository.delete(file); // удаляем запись в таблице files
        }
    }

}