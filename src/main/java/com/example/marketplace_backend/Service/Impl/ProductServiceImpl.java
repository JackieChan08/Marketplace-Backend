package com.example.marketplace_backend.Service.Impl;

import com.example.marketplace_backend.DTO.Requests.models.*;
import com.example.marketplace_backend.DTO.Requests.models.LaptopRequest.*;
import com.example.marketplace_backend.DTO.Requests.models.TableRequest.TableMemoryRequest;
import com.example.marketplace_backend.DTO.Requests.models.TableRequest.TableModuleRequest;
import com.example.marketplace_backend.DTO.Requests.models.TableRequest.TableSpecRequest;
import com.example.marketplace_backend.DTO.Requests.models.WatchRequest.DialRequest;
import com.example.marketplace_backend.DTO.Requests.models.WatchRequest.StrapSizeRequest;
import com.example.marketplace_backend.DTO.Requests.models.WatchRequest.WatchSpecRequest;
import com.example.marketplace_backend.DTO.Responses.models.*;
import com.example.marketplace_backend.Model.*;
import com.example.marketplace_backend.Model.Intermediate_objects.ProductColorImage;
import com.example.marketplace_backend.Model.Intermediate_objects.ProductImage;
import com.example.marketplace_backend.Model.Intermediate_objects.ProductStatuses;
import com.example.marketplace_backend.Model.ProductSpec.LaptopSpec.*;
import com.example.marketplace_backend.Model.ProductSpec.PhoneSpec;
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
    private final PhoneSpecRepository phoneSpecRepository;
    private final LaptopSpecRepository laptopSpecRepository;
    private final ProductVariantRepository productVariantRepository;
    private final ChipRepository chipRepository;
    private final SsdRepository ssdRepository;
    private final RamRepository ramRepository;
    private final WatchSpecRepository watchSpecRepository;
    private final StrapSizeRepository strapSizeRepository;
    private final DialRepository dialRepository;
    private final TableSpecRepository tableSpecRepository;
    private final TableModuleRepository tableModuleRepository;
    private final TableMemoryRepository tableMemoryRepository;

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
                              ProductVariantRepository productVariantRepository,
                              ChipRepository chipRepository,
                              SsdRepository ssdRepository,
                              RamRepository ramRepository,
                              WatchSpecRepository watchSpecRepository,
                              StrapSizeRepository strapSizeRepository,
                              DialRepository dialRepository,
                              TableSpecRepository tableSpecRepository,
                              TableModuleRepository tableModuleRepository,
                              TableMemoryRepository tableMemoryRepository) {
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
        this.chipRepository = chipRepository;
        this.ssdRepository = ssdRepository;
        this.ramRepository = ramRepository;
        this.watchSpecRepository = watchSpecRepository;
        this.strapSizeRepository = strapSizeRepository;
        this.dialRepository = dialRepository;
        this.tableSpecRepository = tableSpecRepository;
        this.tableModuleRepository = tableModuleRepository;
        this.tableMemoryRepository = tableMemoryRepository;
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

        // Обработка цветов и спецификаций
        if (request.getColors() != null && !request.getColors().isEmpty()) {
            for (ColorWithSpecsRequest colorReq : request.getColors()) {
                // Проверка, чтобы одновременно не было нескольких типов спецификаций
                int specCount = 0;
                if (colorReq.getPhoneSpecs() != null && !colorReq.getPhoneSpecs().isEmpty()) specCount++;
                if (colorReq.getChipRequests() != null && !colorReq.getChipRequests().isEmpty()) specCount++;
                if (colorReq.getWatchSpecs() != null && !colorReq.getWatchSpecs().isEmpty()) specCount++;
                if (colorReq.getTableSpecs() != null && !colorReq.getTableSpecs().isEmpty()) specCount++;

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

                // Если есть телефонные спецификации
                if (colorReq.getPhoneSpecs() != null && !colorReq.getPhoneSpecs().isEmpty()) {
                    for (PhoneSpecRequest phoneSpecReq : colorReq.getPhoneSpecs()) {
                        PhoneSpec phoneSpec = PhoneSpec.builder()
                                .memory(phoneSpecReq.getMemory())
                                .price(phoneSpecReq.getPrice())
                                .simType(phoneSpecReq.getSimType())
                                .build();
                        PhoneSpec savedPhoneSpec = phoneSpecRepository.save(phoneSpec);

                        ProductVariant variant = new ProductVariant();
                        variant.setProduct(savedProduct);
                        variant.setColor(savedColor);
                        variant.setPhoneSpec(savedPhoneSpec);
                        productVariantRepository.save(variant);
                    }
                }
                // Если есть спецификации для ноутбука
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

                // Если есть часовые спецификации
                else if (colorReq.getWatchSpecs() != null && !colorReq.getWatchSpecs().isEmpty()) {
                    for (WatchSpecRequest watchSpecReq : colorReq.getWatchSpecs()) {

                        // Если есть strapSizes
                        if (watchSpecReq.getStrapSizes() != null && !watchSpecReq.getStrapSizes().isEmpty()) {
                            for (StrapSizeRequest strapSizeReq : watchSpecReq.getStrapSizes()) {
                                String strapSizeName = strapSizeReq.getName();

                                // Если есть dials — развертываем комбинации strapSize × dial
                                if (strapSizeReq.getDials() != null && !strapSizeReq.getDials().isEmpty()) {
                                    for (DialRequest dialReq : strapSizeReq.getDials()) {
                                        // Парсим size_mm (в DTO — String), безопасно
                                        BigDecimal sizeMm = null;
                                        if (dialReq.getSize_mm() != null && !dialReq.getSize_mm().trim().isEmpty()) {
                                            try {
                                                // Заменяем запятую на точку — на случай локализаций
                                                sizeMm = new BigDecimal(dialReq.getSize_mm().trim().replace(',', '.'));
                                            } catch (NumberFormatException ex) {
                                                // можно логировать, но не бросаем исключение — сохраняем null
                                                // logger.warn("Cannot parse size_mm: {}", dialReq.getSize_mm(), ex);
                                            }
                                        }

                                        BigDecimal price = dialReq.getPrice(); // уже BigDecimal

                                        WatchSpec watchSpec = WatchSpec.builder()
                                                .strapSize(strapSizeName)
                                                .sizeMm(sizeMm)
                                                .price(price)
                                                .build();

                                        WatchSpec savedWatchSpec = watchSpecRepository.save(watchSpec);

                                        ProductVariant variant = new ProductVariant();
                                        variant.setProduct(savedProduct);
                                        variant.setColor(savedColor);
                                        variant.setWatchSpec(savedWatchSpec);
                                        productVariantRepository.save(variant);
                                    }
                                } else {
                                    // Нет dials — создаём одну запись с strapSize и пустыми size/price
                                    WatchSpec watchSpec = WatchSpec.builder()
                                            .strapSize(strapSizeName)
                                            .sizeMm(null)
                                            .price(null)
                                            .build();

                                    WatchSpec savedWatchSpec = watchSpecRepository.save(watchSpec);

                                    ProductVariant variant = new ProductVariant();
                                    variant.setProduct(savedProduct);
                                    variant.setColor(savedColor);
                                    variant.setWatchSpec(savedWatchSpec);
                                    productVariantRepository.save(variant);
                                }
                            }
                        } else {
                            // Нет strapSizes — fallback: создаём одну запись.
                            // Используем title как подпись группы (если нужно), иначе можно поставить null.
                            WatchSpec watchSpec = WatchSpec.builder()
                                    .strapSize(watchSpecReq.getTitle()) // или null, если title не подходит
                                    .sizeMm(null)
                                    .price(null)
                                    .build();

                            WatchSpec savedWatchSpec = watchSpecRepository.save(watchSpec);

                            ProductVariant variant = new ProductVariant();
                            variant.setProduct(savedProduct);
                            variant.setColor(savedColor);
                            variant.setWatchSpec(savedWatchSpec);
                            productVariantRepository.save(variant);
                        }
                    }
                }

                // Если есть спецификации планшета
                else if (colorReq.getTableSpecs() != null && !colorReq.getTableSpecs().isEmpty()) {
                    for (TableSpecRequest tableSpecReq : colorReq.getTableSpecs()) {
                        TableSpec tableSpec = TableSpec.builder()
                                .title(tableSpecReq.getTitle())
                                .tableModules(new ArrayList<>())
                                .build();
                        TableSpec savedTableSpec = tableSpecRepository.save(tableSpec);

                        // Обработка модулей
                        if (tableSpecReq.getModules() != null && !tableSpecReq.getModules().isEmpty()) {
                            for (TableModuleRequest moduleReq : tableSpecReq.getModules()) {
                                TableModule tableModule = TableModule.builder()
                                        .name(moduleReq.getName())
                                        .tableSpec(savedTableSpec)
                                        .memories(new ArrayList<>())
                                        .build();
                                TableModule savedModule = tableModuleRepository.save(tableModule);

                                // Обработка памяти
                                if (moduleReq.getMemories() != null && !moduleReq.getMemories().isEmpty()) {
                                    for (TableMemoryRequest memoryReq : moduleReq.getMemories()) {
                                        TableMemory tableMemory = TableMemory.builder()
                                                .name(memoryReq.getName())
                                                .price(new BigDecimal(memoryReq.getPrice()))
                                                .tableModule(savedModule)
                                                .build();
                                        tableMemoryRepository.save(tableMemory);
                                    }
                                }
                            }
                        }

                        ProductVariant variant = new ProductVariant();
                        variant.setProduct(savedProduct);
                        variant.setColor(savedColor);
                        variant.setTableSpec(savedTableSpec);
                        productVariantRepository.save(variant);
                    }
                }
                // Если нет спецификаций — создаём вариант с цветом без спецификации
                else {
                    ProductVariant variant = new ProductVariant();
                    variant.setProduct(savedProduct);
                    variant.setColor(savedColor);
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
            }
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

//    @Transactional
//    public Product editProduct(UUID id, ProductRequest dto) throws Exception {
//        Product product = productRepository.findById(id)
//                .orElseThrow(() -> new RuntimeException("Product not found"));
//
//        // Обновляем базовые поля
//        if (dto.getName() != null) product.setName(dto.getName());
//        if (dto.getPrice() != null) product.setPrice(dto.getPrice());
//        if (dto.getPriceDescription() != null) product.setPriceDescription(dto.getPriceDescription());
//        if (dto.getTitle() != null) product.setTitle(dto.getTitle());
//        if (dto.getDescription() != null) product.setDescription(dto.getDescription());
//        product.setAvailability(dto.isAvailability());
//
//        if (dto.getSubCategoryId() != null) {
//            product.setSubcategory(subcategoryRepository.findById(dto.getSubCategoryId())
//                    .orElseThrow(() -> new RuntimeException("Subcategory not found")));
//        }
//
//        if (dto.getBrandId() != null) {
//            product.setBrand(brandRepository.findById(dto.getBrandId())
//                    .orElseThrow(() -> new RuntimeException("Brand not found")));
//        }
//
//        // Обновляем статусы
//        if (dto.getStatusId() != null) {
//            productStatusRepository.deleteByProductId(product.getId());
//            for (UUID statusId : dto.getStatusId()) {
//                Statuses status = statusRepository.findById(statusId)
//                        .orElseThrow(() -> new RuntimeException("Status not found with ID: " + statusId));
//                ProductStatuses ps = ProductStatuses.builder()
//                        .product(product)
//                        .status(status)
//                        .build();
//                productStatusRepository.save(ps);
//            }
//        }
//
//        product.setUpdatedAt(LocalDateTime.now());
//        Product updatedProduct = productRepository.save(product);
//
//        // Обновление вариантов (colors + specs)
//        if (dto.getColors() != null) {
//            // Получаем старые варианты продукта
//            List<ProductVariant> existingVariants = productVariantRepository.findByProductId(product.getId());
//
//            // Удаляем старые варианты и связанные данные
//            for (ProductVariant variant : existingVariants) {
//                if (variant.getColor() != null) {
//                    productColorImageRepository.deleteByColorId(variant.getColor().getId());
//                    productColorRepository.deleteById(variant.getColor().getId());
//                }
//                if (variant.getPhoneSpec() != null) {
//                    phoneSpecRepository.deleteById(variant.getPhoneSpec().getId());
//                }
//                if (variant.getLaptopSpec() != null) {
//                    // Удаляем всю вложенную структуру laptop spec
//                    LaptopSpec laptopSpec = variant.getLaptopSpec();
//                    if (laptopSpec.getChips() != null) {
//                        for (Chip chip : laptopSpec.getChips()) {
//                            if (chip.getSsds() != null) {
//                                for (Ssd ssd : chip.getSsds()) {
//                                    if (ssd.getRams() != null) {
//                                        ramRepository.deleteAll(ssd.getRams());
//                                    }
//                                    ssdRepository.delete(ssd);
//                                }
//                            }
//                            chipRepository.delete(chip);
//                        }
//                    }
//                    laptopSpecRepository.deleteById(laptopSpec.getId());
//                }
//
//                // Удаляем всю вложенную структуру watch spec
//                if (variant.getWatchSpec() != null) {
//                    WatchSpec watchSpec = variant.getWatchSpec();
//
//                    // Отвязываем чтобы избежать FK-ошибок
//                    variant.setWatchSpec(null);
//                    productVariantRepository.save(variant);
//
//                    // Дополнительно: если watchSpec может быть разделяемым между variant'ами,
//                    // лучше сначала проверить, есть ли ещё variants с этой watchSpec.
//                    // long count = productVariantRepository.countByWatchSpec_Id(watchSpec.getId());
//                    // if (count == 0) { watchSpecRepository.deleteById(watchSpec.getId()); }
//
//                    watchSpecRepository.deleteById(watchSpec.getId());
//                }
//
//                if (variant.getTableSpec() != null) {
//                    // Удаляем всю вложенную структуру table spec
//                    TableSpec tableSpec = variant.getTableSpec();
//                    if (tableSpec.getTableModules() != null) {
//                        for (TableModule module : tableSpec.getTableModules()) {
//                            if (module.getMemories() != null) {
//                                tableMemoryRepository.deleteAll(module.getMemories());
//                            }
//                            tableModuleRepository.delete(module);
//                        }
//                    }
//                    tableSpecRepository.deleteById(tableSpec.getId());
//                }
//            }
//            productVariantRepository.deleteByProductId(product.getId());
//
//            // Создание новых вариантов
//            for (ColorWithSpecsRequest colorReq : dto.getColors()) {
//                // Проверка, чтобы одновременно не было нескольких типов спецификаций
//                int specCount = 0;
//                if (colorReq.getPhoneSpecs() != null && !colorReq.getPhoneSpecs().isEmpty()) specCount++;
//                if (colorReq.getLaptopSpecs() != null && !colorReq.getLaptopSpecs().isEmpty()) specCount++;
//                if (colorReq.getWatchSpecs() != null && !colorReq.getWatchSpecs().isEmpty()) specCount++;
//                if (colorReq.getTableSpecs() != null && !colorReq.getTableSpecs().isEmpty()) specCount++;
//
//                if (specCount > 1) {
//                    throw new RuntimeException("Color " + colorReq.getName() + " can only have one type of specification");
//                }
//
//                // Создаём цвет
//                ProductColor color = ProductColor.builder()
//                        .name(colorReq.getName())
//                        .hex(colorReq.getHex())
//                        .price(colorReq.getPrice())
//                        .build();
//                ProductColor savedColor = productColorRepository.save(color);
//
//                // Сохраняем изображения цвета
//                if (colorReq.getImages() != null) {
//                    for (MultipartFile imageFile : colorReq.getImages()) {
//                        FileEntity fileEntity = fileUploadService.saveImage(imageFile);
//                        ProductColorImage colorImage = ProductColorImage.builder()
//                                .color(savedColor)
//                                .image(fileEntity)
//                                .build();
//                        productColorImageRepository.save(colorImage);
//                    }
//                }
//
//                // Варианты с телефонными спецификациями
//                if (colorReq.getPhoneSpecs() != null && !colorReq.getPhoneSpecs().isEmpty()) {
//                    for (PhoneSpecRequest phoneSpecReq : colorReq.getPhoneSpecs()) {
//                        PhoneSpec phoneSpec = PhoneSpec.builder()
//                                .memory(phoneSpecReq.getMemory())
//                                .price(phoneSpecReq.getPrice())
//                                .simType(phoneSpecReq.getSimType())
//                                .build();
//                        PhoneSpec savedPhoneSpec = phoneSpecRepository.save(phoneSpec);
//
//                        ProductVariant variant = new ProductVariant();
//                        variant.setProduct(updatedProduct);
//                        variant.setColor(savedColor);
//                        variant.setPhoneSpec(savedPhoneSpec);
//                        productVariantRepository.save(variant);
//                    }
//                }
//                // Варианты с ноутбучными спецификациями и вложенной структурой
//                else if (colorReq.getLaptopSpecs() != null && !colorReq.getLaptopSpecs().isEmpty()) {
//                    for (LaptopSpecRequest laptopSpecReq : colorReq.getLaptopSpecs()) {
//                        LaptopSpec laptopSpec = LaptopSpec.builder()
//                                .chips(new ArrayList<>())
//                                .build();
//                        LaptopSpec savedLaptopSpec = laptopSpecRepository.save(laptopSpec);
//
//                        // Обработка чипов
//                        if (laptopSpecReq.getChips() != null && !laptopSpecReq.getChips().isEmpty()) {
//                            for (ChipRequest chipReq : laptopSpecReq.getChips()) {
//                                Chip chip = Chip.builder()
//                                        .name(chipReq.getName())
//                                        .laptopSpec(savedLaptopSpec)
//                                        .ssds(new ArrayList<>())
//                                        .build();
//                                Chip savedChip = chipRepository.save(chip);
//
//                                // Обработка SSD
//                                if (chipReq.getSsdRequests() != null && !chipReq.getSsdRequests().isEmpty()) {
//                                    for (SsdRequest ssdReq : chipReq.getSsdRequests()) {
//                                        Ssd ssd = Ssd.builder()
//                                                .name(ssdReq.getName())
//                                                .chip(savedChip)
//                                                .rams(new ArrayList<>())
//                                                .build();
//                                        Ssd savedSsd = ssdRepository.save(ssd);
//
//                                        // Обработка RAM
//                                        if (ssdReq.getRamRequests() != null && !ssdReq.getRamRequests().isEmpty()) {
//                                            for (RamRequest ramReq : ssdReq.getRamRequests()) {
//                                                Ram ram = Ram.builder()
//                                                        .name(ramReq.getName())
//                                                        .price(new BigDecimal(ramReq.getPrice()))
//                                                        .ssd(savedSsd)
//                                                        .build();
//                                                ramRepository.save(ram);
//                                            }
//                                        }
//                                    }
//                                }
//                            }
//                        }
//
//                        ProductVariant variant = new ProductVariant();
//                        variant.setProduct(updatedProduct);
//                        variant.setColor(savedColor);
//                        variant.setLaptopSpec(savedLaptopSpec);
//                        productVariantRepository.save(variant);
//                    }
//                }
//                // Варианты с часовыми спецификациями
//                else if (colorReq.getWatchSpecs() != null && !colorReq.getWatchSpecs().isEmpty()) {
//                    for (WatchSpecRequest watchSpecReq : colorReq.getWatchSpecs()) {
//
//                        // Перебираем strapSizes
//                        if (watchSpecReq.getStrapSizes() != null && !watchSpecReq.getStrapSizes().isEmpty()) {
//                            for (StrapSizeRequest strapSizeReq : watchSpecReq.getStrapSizes()) {
//                                String strapSizeName = strapSizeReq.getName();
//
//                                // Перебираем dials
//                                if (strapSizeReq.getDials() != null && !strapSizeReq.getDials().isEmpty()) {
//                                    for (DialRequest dialReq : strapSizeReq.getDials()) {
//                                        BigDecimal sizeMm = null;
//                                        if (dialReq.getSize_mm() != null && !dialReq.getSize_mm().trim().isEmpty()) {
//                                            try {
//                                                sizeMm = new BigDecimal(dialReq.getSize_mm().trim().replace(',', '.'));
//                                            } catch (NumberFormatException e) {
//                                                // можно залогировать
//                                            }
//                                        }
//
//                                        WatchSpec watchSpec = WatchSpec.builder()
//                                                .strapSize(strapSizeName)
//                                                .sizeMm(sizeMm)
//                                                .price(dialReq.getPrice())
//                                                .build();
//
//                                        WatchSpec savedWatchSpec = watchSpecRepository.save(watchSpec);
//
//                                        ProductVariant variant = new ProductVariant();
//                                        variant.setProduct(updatedProduct);
//                                        variant.setColor(savedColor);
//                                        variant.setWatchSpec(savedWatchSpec);
//                                        productVariantRepository.save(variant);
//                                    }
//                                } else {
//                                    // StrapSize без dials → создаём одну запись
//                                    WatchSpec watchSpec = WatchSpec.builder()
//                                            .strapSize(strapSizeName)
//                                            .sizeMm(null)
//                                            .price(null)
//                                            .build();
//
//                                    WatchSpec savedWatchSpec = watchSpecRepository.save(watchSpec);
//
//                                    ProductVariant variant = new ProductVariant();
//                                    variant.setProduct(updatedProduct);
//                                    variant.setColor(savedColor);
//                                    variant.setWatchSpec(savedWatchSpec);
//                                    productVariantRepository.save(variant);
//                                }
//                            }
//                        } else {
//                            // Нет strapSizes → fallback: создаём один WatchSpec
//                            WatchSpec watchSpec = WatchSpec.builder()
//                                    .strapSize(watchSpecReq.getTitle()) // или null, если title не нужен
//                                    .sizeMm(null)
//                                    .price(null)
//                                    .build();
//
//                            WatchSpec savedWatchSpec = watchSpecRepository.save(watchSpec);
//
//                            ProductVariant variant = new ProductVariant();
//                            variant.setProduct(updatedProduct);
//                            variant.setColor(savedColor);
//                            variant.setWatchSpec(savedWatchSpec);
//                            productVariantRepository.save(variant);
//                        }
//                    }
//                }
//
//                // Варианты со столовыми спецификациями
//                else if (colorReq.getTableSpecs() != null && !colorReq.getTableSpecs().isEmpty()) {
//                    for (TableSpecRequest tableSpecReq : colorReq.getTableSpecs()) {
//                        TableSpec tableSpec = TableSpec.builder()
//                                .title(tableSpecReq.getTitle())
//                                .tableModules(new ArrayList<>())
//                                .build();
//                        TableSpec savedTableSpec = tableSpecRepository.save(tableSpec);
//
//                        // Обработка модулей
//                        if (tableSpecReq.getModules() != null && !tableSpecReq.getModules().isEmpty()) {
//                            for (TableModuleRequest moduleReq : tableSpecReq.getModules()) {
//                                TableModule tableModule = TableModule.builder()
//                                        .name(moduleReq.getName())
//                                        .tableSpec(savedTableSpec)
//                                        .memories(new ArrayList<>())
//                                        .build();
//                                TableModule savedModule = tableModuleRepository.save(tableModule);
//
//                                // Обработка памяти
//                                if (moduleReq.getMemories() != null && !moduleReq.getMemories().isEmpty()) {
//                                    for (TableMemoryRequest memoryReq : moduleReq.getMemories()) {
//                                        TableMemory tableMemory = TableMemory.builder()
//                                                .name(memoryReq.getName())
//                                                .price(new BigDecimal(memoryReq.getPrice()))
//                                                .tableModule(savedModule)
//                                                .build();
//                                        tableMemoryRepository.save(tableMemory);
//                                    }
//                                }
//                            }
//                        }
//
//                        ProductVariant variant = new ProductVariant();
//                        variant.setProduct(updatedProduct);
//                        variant.setColor(savedColor);
//                        variant.setTableSpec(savedTableSpec);
//                        productVariantRepository.save(variant);
//                    }
//                }
//                // Если нет спецификаций — вариант только с цветом
//                else {
//                    ProductVariant variant = new ProductVariant();
//                    variant.setProduct(updatedProduct);
//                    variant.setColor(savedColor);
//                    productVariantRepository.save(variant);
//                }
//            }
//        } else if (dto.getImages() != null && !dto.getImages().isEmpty()) {
//            // Если нет цветов, но есть общие изображения
//            productImageRepository.deleteByProductId(product.getId());
//            for (MultipartFile imageFile : dto.getImages()) {
//                FileEntity fileEntity = fileUploadService.saveImage(imageFile);
//                ProductImage productImage = ProductImage.builder()
//                        .product(updatedProduct)
//                        .image(fileEntity)
//                        .build();
//                productImageRepository.save(productImage);
//            }
//        }
//
//        // Обновление параметров
//        ObjectMapper mapper = new ObjectMapper();
//        List<ProductParameterRequest> parameters = new ArrayList<>();
//        if (dto.getParametersJson() != null && !dto.getParametersJson().isEmpty()) {
//            parameters = mapper.readValue(
//                    dto.getParametersJson(),
//                    new TypeReference<List<ProductParameterRequest>>() {}
//            );
//        }
//
//        if (!parameters.isEmpty()) {
//            productParametersRepository.deleteByProductId(product.getId());
//
//            for (ProductParameterRequest paramReq : parameters) {
//                ProductParameters parameter = new ProductParameters();
//                parameter.setName(paramReq.getName());
//                parameter.setProduct(updatedProduct);
//
//                List<ProductSubParameters> subParams = new ArrayList<>();
//                if (paramReq.getSubParameters() != null) {
//                    for (ProductSubParameterRequest sub : paramReq.getSubParameters()) {
//                        ProductSubParameters s = new ProductSubParameters();
//                        s.setName(sub.getName());
//                        s.setValue(sub.getValue());
//                        s.setProductParameter(parameter);
//                        subParams.add(s);
//                    }
//                }
//
//                parameter.setProductSubParameters(subParams);
//                productParametersRepository.save(parameter);
//            }
//        }
//
//        return productRepository.findByIdWithImagesAndStatuses(updatedProduct.getId())
//                .orElseThrow(() -> new RuntimeException("Product not found after update"));
//    }


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

}