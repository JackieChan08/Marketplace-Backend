package com.example.marketplace_backend.Service.Impl;

import com.example.marketplace_backend.DTO.Responses.models.*;
import com.example.marketplace_backend.DTO.Responses.models.ImageReponse.BrandImageResponse;
import com.example.marketplace_backend.DTO.Responses.models.ImageReponse.CategoryIconResponse;
import com.example.marketplace_backend.DTO.Responses.models.ImageReponse.CategoryImageResponse;
import com.example.marketplace_backend.DTO.Responses.models.ImageReponse.FileResponse;
import com.example.marketplace_backend.DTO.Responses.models.LaptopResponse.ChipResponse;
import com.example.marketplace_backend.DTO.Responses.models.LaptopResponse.RamResponse;
import com.example.marketplace_backend.DTO.Responses.models.LaptopResponse.SsdResponse;
import com.example.marketplace_backend.DTO.Responses.models.PhoneSpecResponse.PhoneMemoryResponse;
import com.example.marketplace_backend.DTO.Responses.models.PhoneSpecResponse.SimTypeResponse;
import com.example.marketplace_backend.DTO.Responses.models.TableResponse.TableMemoryResponse;
import com.example.marketplace_backend.DTO.Responses.models.TableResponse.TableModuleResponse;
import com.example.marketplace_backend.DTO.Responses.models.WatchResponse.DialResponse;
import com.example.marketplace_backend.DTO.Responses.models.WatchResponse.StrapSizeResponse;
import com.example.marketplace_backend.Model.*;
import com.example.marketplace_backend.Model.Intermediate_objects.*;
import com.example.marketplace_backend.Model.ProductSpec.LaptopSpec.Chip;
import com.example.marketplace_backend.Model.ProductSpec.LaptopSpec.LaptopSpec;
import com.example.marketplace_backend.Model.ProductSpec.LaptopSpec.Ram;
import com.example.marketplace_backend.Model.ProductSpec.LaptopSpec.Ssd;
import com.example.marketplace_backend.Model.ProductSpec.PhoneSpec.PhoneMemory;
import com.example.marketplace_backend.Model.ProductSpec.PhoneSpec.PhoneSpec;
import com.example.marketplace_backend.Model.ProductSpec.PhoneSpec.SimType;
import com.example.marketplace_backend.Model.ProductSpec.TableSpec.TableMemory;
import com.example.marketplace_backend.Model.ProductSpec.TableSpec.TableModule;
import com.example.marketplace_backend.Model.ProductSpec.TableSpec.TableSpec;
import com.example.marketplace_backend.Model.ProductSpec.WatchSpec.Dial;
import com.example.marketplace_backend.Model.ProductSpec.WatchSpec.StrapSize;
import com.example.marketplace_backend.Model.ProductSpec.WatchSpec.WatchSpec;
import com.example.marketplace_backend.Repositories.ProductColorRepository;
import com.example.marketplace_backend.Repositories.ProductVariantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ConverterService {
    private final ProductParametersServiceImpl productParametersService;
    private final ProductColorRepository productColorRepository;
    private final ProductVariantRepository productVariantRepository;

    @Value("${app.base-url}")
    private String baseUrl;

    public ProductResponse convertToProductResponse(Product product) {
        if (product == null) return null;

        ProductResponse response = ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .price(product.getPrice())
                .priceDescription(product.getPriceDescription())
                .discountedPrice(product.getDiscountedPrice())
                .availability(product.isAvailability())
                .title(product.getTitle())
                .description(product.getDescription())
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .deletedAt(product.getDeletedAt())
                .build();

        // Subcategory / Category
        if (product.getSubcategory() != null) {
            response.setSubcategoryId(product.getSubcategory().getId());
            response.setSubcategoryName(product.getSubcategory().getName());
            if (product.getSubcategory().getCategory() != null) {
                response.setCategoryId(product.getSubcategory().getCategory().getId());
                response.setCategoryName(product.getSubcategory().getCategory().getName());
            }
        }

        // Brand
        if (product.getBrand() != null) {
            response.setBrandId(product.getBrand().getId());
            response.setBrandName(product.getBrand().getName());
        }

        // Colors
        response.setColors(convertToColorTree(product.getVariants()));

        // Images: приоритет — изображения из вариантов (цветов), иначе общие product images
        List<FileResponse> variantImages = product.getVariants() != null
                ? product.getVariants().stream()
                .filter(v -> v.getColor() != null && v.getColor().getImages() != null)
                .flatMap(v -> v.getColor().getImages().stream()
                        .map(img -> convertToFileResponse(img.getImage())))
                .collect(Collectors.toMap(
                        FileResponse::getId, // ключ = id файла
                        f -> f,
                        (f1, f2) -> f1 // при дубликате берём первый
                ))
                .values()
                .stream()
                .toList()
                : List.of();

        List<FileResponse> productImages = (product.getImages() != null)
                ? product.getImages().stream()
                .map(img -> convertToFileResponse(img.getImage()))
                .collect(Collectors.toMap(
                        FileResponse::getId,
                        f -> f,
                        (f1, f2) -> f1
                ))
                .values()
                .stream()
                .toList()
                : List.of();

        response.setImages(!variantImages.isEmpty() ? variantImages : productImages);

        // Statuses
        if (product.getProductStatuses() != null) {
            List<StatusResponse> statuses = product.getProductStatuses().stream()
                    .map(ps -> {
                        Statuses st = ps.getStatus();
                        return StatusResponse.builder()
                                .id(st.getId())
                                .name(st.getName())
                                .primaryColor(st.getPrimaryColor())
                                .backgroundColor(st.getBackgroundColor())
                                .build();
                    }).collect(Collectors.toList());
            response.setStatuses(statuses);
        } else {
            response.setStatuses(List.of());
        }

        // Parameters (через сервис параметров)
        List<ProductParameterResponse> params = productParametersService
                .getParametersWithSubParams(product.getId())
                .stream()
                .map(param -> ProductParameterResponse.builder()
                        .id(param.getId())
                        .name(param.getName())
                        .subParameters(param.getProductSubParameters().stream()
                                .map(sp -> ProductSubParameterResponse.builder()
                                        .id(sp.getId())
                                        .name(sp.getName())
                                        .value(sp.getValue())
                                        .build())
                                .collect(Collectors.toList()))
                        .build())
                .collect(Collectors.toList());
        response.setParameters(params);
        return response;
    }


    /* ---------- вспомогательные методы ---------- */
    private FileResponse convertToFileResponse(FileEntity image) {
        if (image == null) return null;
        return FileResponse.builder()
                .id(image.getId())
                .uniqueName(image.getUniqueName())
                .originalName(image.getOriginalName())
                .url(baseUrl + "/uploads/" + image.getUniqueName())
                .fileType(image.getFileType())
                .build();
    }

    private ColorResponse convertToColorResponse(ProductColor color) {
        if (color == null) return null;

        List<FileResponse> images = (color.getImages() != null)
                ? color.getImages().stream()
                .map(img -> convertToFileResponse(img.getImage()))
                .collect(Collectors.toList())
                : List.of();

        return ColorResponse.builder()
                .id(color.getId())
                .name(color.getName())
                .hex(color.getHex())
                .price(color.getPrice())
                .images(images)
                .build();
    }

    private List<ChipResponse> convertToChipResponse(LaptopSpec laptopSpec) {
        if (laptopSpec == null) {
            return Collections.emptyList();
        }

        Map<UUID, ChipResponse> chipMap = new LinkedHashMap<>();

        // --- CHIP ---
        Chip chip = laptopSpec.getChip();
        if (chip == null) {
            return Collections.emptyList();
        }

        ChipResponse chipResp = chipMap.computeIfAbsent(
                chip.getId(),
                id -> ChipResponse.builder()
                        .id(chip.getId())
                        .name(chip.getName())
                        .ssdResponses(new ArrayList<>())
                        .build()
        );

        // --- SSD ---
        Ssd ssd = laptopSpec.getSsd();
        if (ssd != null) {
            SsdResponse ssdResp = chipResp.getSsdResponses().stream()
                    .filter(s -> Objects.equals(s.getId(), ssd.getId()))
                    .findFirst()
                    .orElseGet(() -> {
                        SsdResponse newSsd = SsdResponse.builder()
                                .id(ssd.getId())
                                .name(ssd.getName())
                                .rams(new ArrayList<>())
                                .build();
                        chipResp.getSsdResponses().add(newSsd);
                        return newSsd;
                    });

            // --- RAM ---
            Ram ram = laptopSpec.getRam();
            if (ram != null && ssdResp.getRams().stream().noneMatch(r -> Objects.equals(r.getId(), ram.getId()))) {
                ProductVariant variant = productVariantRepository.findByLaptopSpecId(laptopSpec.getId());

                UUID variantId = (variant != null) ? variant.getId() : null;

                ssdResp.getRams().add(
                        RamResponse.builder()
                                .id(ram.getId())
                                .name(ram.getName())
                                .price(ram.getPrice())
                                .productVariantId(variantId)
                                .build()
                );
            }
        }

        return new ArrayList<>(chipMap.values());
    }

    // Конвертация для PhoneSpec: SimType -> PhoneMemory
    private List<SimTypeResponse> convertToSimTypeResponse(PhoneSpec phoneSpec) {
        if (phoneSpec == null) {
            return Collections.emptyList();
        }

        Map<UUID, SimTypeResponse> simTypeMap = new LinkedHashMap<>();

        // --- SIM_TYPE ---
        SimType simType = phoneSpec.getSimType();
        if (simType == null) {
            return Collections.emptyList();
        }

        SimTypeResponse simTypeResp = simTypeMap.computeIfAbsent(
                simType.getId(),
                id -> SimTypeResponse.builder()
                        .id(simType.getId())
                        .name(simType.getName())
                        .phoneMemoryResponses(new ArrayList<>())
                        .build()
        );

        // --- PHONE_MEMORY ---
        PhoneMemory phoneMemory = phoneSpec.getPhoneMemory();
        if (phoneMemory != null && simTypeResp.getPhoneMemoryResponses().stream()
                .noneMatch(pm -> Objects.equals(pm.getId(), phoneMemory.getId()))) {
            simTypeResp.getPhoneMemoryResponses().add(
                    PhoneMemoryResponse.builder()
                            .id(phoneMemory.getId())
                            .name(phoneMemory.getName())
                            .price(phoneMemory.getPrice())
                            .build()
            );
        }

        return new ArrayList<>(simTypeMap.values());
    }

    // Конвертация для TableSpec: TableModule -> TableMemory
    private List<TableModuleResponse> convertToTableModuleResponse(TableSpec tableSpec) {
        if (tableSpec == null) {
            return Collections.emptyList();
        }

        Map<UUID, TableModuleResponse> moduleMap = new LinkedHashMap<>();

        // --- TABLE_MODULE ---
        TableModule tableModule = tableSpec.getTableModule();
        if (tableModule == null) {
            return Collections.emptyList();
        }

        TableModuleResponse moduleResp = moduleMap.computeIfAbsent(
                tableModule.getId(),
                id -> TableModuleResponse.builder()
                        .id(tableModule.getId())
                        .name(tableModule.getName())
                        .tableMemoryResponses(new ArrayList<>())
                        .build()
        );

        // --- TABLE_MEMORY ---
        TableMemory tableMemory = tableSpec.getTableMemory();
        if (tableMemory != null && moduleResp.getTableMemoryResponses().stream()
                .noneMatch(tm -> Objects.equals(tm.getId(), tableMemory.getId()))) {
            moduleResp.getTableMemoryResponses().add(
                    TableMemoryResponse.builder()
                            .id(tableMemory.getId())
                            .name(tableMemory.getName())
                            .price(tableMemory.getPrice())
                            .build()
            );
        }

        return new ArrayList<>(moduleMap.values());
    }

    // Конвертация для WatchSpec: StrapSize -> Dial
    private List<StrapSizeResponse> convertToStrapSizeResponse(WatchSpec watchSpec) {
        if (watchSpec == null) {
            return Collections.emptyList();
        }

        Map<UUID, StrapSizeResponse> strapSizeMap = new LinkedHashMap<>();

        // --- STRAP_SIZE ---
        StrapSize strapSize = watchSpec.getStrapSize();
        if (strapSize == null) {
            return Collections.emptyList();
        }

        StrapSizeResponse strapSizeResp = strapSizeMap.computeIfAbsent(
                strapSize.getId(),
                id -> StrapSizeResponse.builder()
                        .id(strapSize.getId())
                        .name(strapSize.getName())
                        .dials(new ArrayList<>())
                        .build()
        );

        // --- DIAL ---
        Dial dial = watchSpec.getDial();
        if (dial != null && strapSizeResp.getDials().stream()
                .noneMatch(d -> Objects.equals(d.getId(), dial.getId()))) {
            strapSizeResp.getDials().add(
                    DialResponse.builder()
                            .id(dial.getId())
                            .name(dial.getName())
                            .price(dial.getPrice())
                            .build()
            );
        }

        return new ArrayList<>(strapSizeMap.values());
    }

    public SubcategoryResponse convertToSubcategoryResponse(Subcategory subcategory) {
        SubcategoryResponse response = new SubcategoryResponse();
        response.setId(subcategory.getId());
        response.setName(subcategory.getName());

        if (subcategory.getCategory() != null) {
            response.setCategoryId(subcategory.getCategory().getId());
            response.setCategoryName(subcategory.getCategory().getName());
        }

        return response;
    }

    public CategoryResponse convertToCategoryResponse(Category category) {
        CategoryResponse response = new CategoryResponse();
        response.setId(category.getId());
        response.setName(category.getName());
        response.setCreatedAt(category.getCreatedAt());
        response.setUpdatedAt(category.getUpdatedAt());
        response.setDeletedAt(category.getDeletedAt());
        response.setPriority(category.isPriority());

        // Process category image
        if (category.getCategoryImages() != null && !category.getCategoryImages().isEmpty()) {
            CategoryImage firstImage = category.getCategoryImages().iterator().next();
            CategoryImageResponse categoryImageResponse = new CategoryImageResponse();
            categoryImageResponse.setId(firstImage.getId());
            categoryImageResponse.setImage(convertToFileResponse(firstImage.getImage()));
            response.setCategoryImage(categoryImageResponse);
        }

        // Process category icon
        if (category.getCategoryIcons() != null && !category.getCategoryIcons().isEmpty()) {
            CategoryIcon firstIcon = category.getCategoryIcons().iterator().next();
            CategoryIconResponse categoryIconResponse = new CategoryIconResponse();
            categoryIconResponse.setId(firstIcon.getId());
            categoryIconResponse.setIcon(convertToFileResponse(firstIcon.getIcon()));
            response.setCategoryIcon(categoryIconResponse);
        }

        if (category.getSubcategories() != null && !category.getSubcategories().isEmpty()) {
            List<SubcategoryResponseSimple> subcategoryResponsesSimple = category.getSubcategories().stream()
                    .filter(subcategory -> subcategory.getDeletedAt() == null)
                    .map(this::convertToSubcategoryResponseSimple)
                    .collect(Collectors.toList());
            response.setSubcategoryResponsesSimple(subcategoryResponsesSimple);
        }

        return response;
    }

    public UserResponse convertToUserResponse(User user) {
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setEmail(user.getEmail());
        response.setName(user.getName());
        response.setPhoneNumber(user.getPhoneNumber());
        response.setCity(user.getCity());
        response.setAddress(user.getAddress());
        response.setRole(user.getRole());
        response.setCreatedAt(user.getCreatedAt());

        if (user.getUserImages() != null && !user.getUserImages().isEmpty()) {
            List<FileResponse> images = user.getUserImages().stream()
                    .map(userImage -> convertToFileResponse(userImage.getImage()))
                    .collect(Collectors.toList());
            response.setImages(images);
        }

        return response;
    }

    public BrandResponse convertToBrandResponse(Brand brand) {
        BrandResponse response = new BrandResponse();
        response.setId(brand.getId());
        response.setName(brand.getName());
        response.setCreatedAt(brand.getCreatedAt());
        response.setUpdatedAt(brand.getUpdatedAt());
        response.setDeletedAt(brand.getDeletedAt());

        if (brand.getBrandImages() != null && !brand.getBrandImages().isEmpty()) {
            BrandImage firstImage = brand.getBrandImages().iterator().next();
            BrandImageResponse brandImageResponse = new BrandImageResponse();
            brandImageResponse.setId(firstImage.getId());
            brandImageResponse.setImage(convertToFileResponse(firstImage.getImage()));
            response.setBrandImage(brandImageResponse);
        }

        return response;
    }

    public OrderResponse convertToOrderResponse(Order order) {
        return OrderResponse.builder()
                .id(order.getId())
                .address(order.getAddress())
                .city(order.getCity())
                .phoneNumber(order.getPhoneNumber())
                .comment(order.getComment())
                .totalPrice(order.getTotalPrice())
                .totalQuantity(order.getOrderItems().size())
                .isWholesale(order.isWholesale())
                .createdAt(order.getCreatedAt())
                .paymentMethod(order.getPaymentMethod())
                .userId(order.getUser().getId())
                .username(order.getUser().getName())
                .orderItems(convertOrderItems(order.getOrderItems()))
                .status(convertStatus(order.getStatus()))
                .orderNumber(order.getOrderNumber())
                .build();
    }

    public OrderWholesaleResponse convertToOrderWholesaleResponse(Order order) {
        return OrderWholesaleResponse.builder()
                .id(order.getId())
                .address(order.getAddress())
                .phoneNumber(order.getPhoneNumber())
                .comment(order.getComment())
                .isWholesale(order.isWholesale())
                .createdAt(order.getCreatedAt())
                .paymentMethod(order.getPaymentMethod())
                .userId(order.getUser().getId())
                .username(order.getUser().getName())
                .status(convertStatus(order.getStatus()))
                .build();
    }

    private List<OrderItemResponse> convertOrderItems(List<OrderItem> items) {
        if (items == null) return List.of();

        return items.stream().map(item -> {
            OrderItemResponse response = new OrderItemResponse();
            response.setId(item.getId());
            response.setProductVariantResponse(convertToProductVariantResponse(item.getProductVariant()));
            response.setQuantity(item.getQuantity());
            response.setPrice(item.getPrice());
            return response;
        }).collect(Collectors.toList());
    }

    private OrderStatusResponse convertStatus(Statuses status) {
        if (status == null) return null;

        OrderStatusResponse response = new OrderStatusResponse();
        response.setId(status.getId());
        response.setName(status.getName());
        response.setPrimaryColor(status.getPrimaryColor());
        response.setBackgroundColor(status.getBackgroundColor());
        return response;
    }

    public VipProductResponse convertToVipProductResponse(VipProduct vipProduct) {
        if (vipProduct == null) {
            return null;
        }

        VipProductResponse response = new VipProductResponse();
        response.setId(vipProduct.getId());
        response.setName(vipProduct.getName());
        response.setImage(vipProduct.getImage());

        return response;
    }

    public CategoryWithSubcategoryResponse convertToCategoryWithSubcategoryResponse(Category category) {
        CategoryWithSubcategoryResponse response = new CategoryWithSubcategoryResponse();
        response.setId(category.getId());
        response.setName(category.getName());

        if (category.getSubcategories() != null && !category.getSubcategories().isEmpty()) {
            List<SubcategoryResponseSimple> subcategoryResponsesSimple = category.getSubcategories().stream()
                    .filter(subcategory -> subcategory.getDeletedAt() == null)
                    .map(this::convertToSubcategoryResponseSimple)
                    .collect(Collectors.toList());
            response.setSubcategoryResponsesSimple(subcategoryResponsesSimple);
        }

        return response;
    }

    public SubcategoryResponseSimple convertToSubcategoryResponseSimple(Subcategory subcategory) {
        SubcategoryResponseSimple response = new SubcategoryResponseSimple();
        response.setId(subcategory.getId());
        response.setName(subcategory.getName());
        return response;
    }

    public FavoriteResponse convertToFavoriteResponse(Favorite favorite) {
        List<FavoriteItemResponse> items = favorite.getFavoriteItems().stream().map(item -> {
            BigDecimal totalPrice = item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
            return FavoriteItemResponse.builder()
                    .productVariant(convertToProductVariantResponse(item.getProductVariant()))
                    .quantity(item.getQuantity())
                    .pricePerItem(item.getPrice())
                    .totalPrice(totalPrice)
                    .favoriteItemId(item.getId())
                    .addedAt(item.getAddedAt())
                    .build();
        }).collect(Collectors.toList());

        BigDecimal total = items.stream()
                .map(FavoriteItemResponse::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return FavoriteResponse.builder()
                .items(items)
                .totalPrice(total)
                .build();
    }

    public FavoriteItemResponse convertToFavoriteItemResponse(FavoriteItem item) {
        BigDecimal totalPrice = item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));

        return FavoriteItemResponse.builder()
                .favoriteItemId(item.getId())
                .quantity(item.getQuantity())
                .pricePerItem(item.getPrice())
                .totalPrice(totalPrice)
                .addedAt(item.getAddedAt())
                .productVariant(convertToProductVariantResponse(item.getProductVariant()))
                .build();
    }

    public CartResponse convertToCartResponse(Cart cart) {
        List<CartItemResponse> items = cart.getCartItems().stream()
                .map(this::convertToCartItemResponse) // используем общий метод
                .collect(Collectors.toList());

        BigDecimal total = items.stream()
                .map(CartItemResponse::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return CartResponse.builder()
                .items(items)
                .totalPrice(total)
                .build();
    }

    public CartItemResponse convertToCartItemResponse(CartItem item) {
        BigDecimal totalPrice = item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));

        return CartItemResponse.builder()
                .cartItemId(item.getId())
                .quantity(item.getQuantity())
                .pricePerItem(item.getPrice())
                .totalPrice(totalPrice)
                .addedAt(item.getCreatedAt())
                .productVariant(convertToProductVariantResponse(item.getProductVariant()))
                .build();
    }

    public OrderItemResponse toOrderItemResponse(OrderItem item) {
        return OrderItemResponse.builder()
                .id(item.getId())
                .productVariantResponse(convertToProductVariantResponse(item.getProductVariant()))
                .quantity(item.getQuantity())
                .price(item.getPrice())
                .build();
    }

    public OrderStatusResponse toOrderStatusResponse(com.example.marketplace_backend.Model.Statuses status) {
        if (status == null) {
            return null;
        }
        return OrderStatusResponse.builder()
                .id(status.getId())
                .name(status.getName())
                .primaryColor(status.getPrimaryColor())
                .backgroundColor(status.getBackgroundColor())
                .build();
    }

    public OrderResponse toOrderResponse(Order order) {
        List<OrderItemResponse> itemResponses = order.getOrderItems().stream()
                .map(this::toOrderItemResponse)
                .collect(Collectors.toList());

        return OrderResponse.builder()
                .id(order.getId())
                .address(order.getAddress())
                .phoneNumber(order.getPhoneNumber())
                .comment(order.getComment())
                .totalPrice(order.getTotalPrice())
                .isWholesale(order.isWholesale())
                .createdAt(order.getCreatedAt())
                .userId(order.getUser().getId())
                .username(order.getUser().getName())
                .paymentMethod(order.getPaymentMethod())
                .orderNumber(order.getOrderNumber())
                .orderItems(itemResponses)
                .status(toOrderStatusResponse(order.getStatus()))
                .build();
    }

    public OrderWholesaleResponse toOrderWholesaleResponse(Order order) {
        return OrderWholesaleResponse.builder()
                .id(order.getId())
                .address(order.getAddress())
                .phoneNumber(order.getPhoneNumber())
                .comment(order.getComment())
                .isWholesale(order.isWholesale())
                .createdAt(order.getCreatedAt())
                .userId(order.getUser().getId())
                .username(order.getUser().getName())
                .paymentMethod(order.getPaymentMethod())
                .orderNumber(order.getOrderNumber())
                .status(toOrderStatusResponse(order.getStatus()))
                .build();
    }

    public ProductParameterResponse convertToProductParameterResponse(ProductParameters parameter) {
        return ProductParameterResponse.builder()
                .id(parameter.getId())
                .name(parameter.getName())
                .subParameters(
                        parameter.getProductSubParameters().stream()
                                .map(sub -> ProductSubParameterResponse.builder()
                                        .id(sub.getId())
                                        .name(sub.getName())
                                        .value(sub.getValue())
                                        .build()
                                )
                                .collect(Collectors.toList())
                )
                .build();
    }

    public StatusResponse convertToStatusResponse(Statuses status) {
        return StatusResponse.builder()
                .id(status.getId())
                .name(status.getName())
                .backgroundColor(status.getBackgroundColor())
                .primaryColor(status.getPrimaryColor())
                .build();
    }

    public ProductVariantResponse convertToProductVariantResponse(ProductVariant variant) {
        if (variant == null) {
            return null;
        }

        return ProductVariantResponse.builder()
                .id(variant.getId())
                .productId(variant.getProduct().getId())
                .color(convertToColorResponse(variant.getColor()))
                .simTypeResponses(
                        variant.getPhoneSpec() != null
                                ? convertToSimTypeResponse(variant.getPhoneSpec())
                                : null
                )
                .chipResponses(
                        variant.getLaptopSpec() != null
                                ? convertToChipResponse(variant.getLaptopSpec())
                                : null
                )
                .strapSizeResponses(
                        variant.getTableSpec() != null
                                ? convertToStrapSizeResponse(variant.getWatchSpec())
                                : null
                )
                .tableModuleResponses(
                        variant.getTableSpec() != null
                                ? convertToTableModuleResponse(variant.getTableSpec())
                                : null
                )
                .build();
    }

    private List<ColorResponse> convertToColorTree(List<ProductVariant> variants) {
        if (variants == null || variants.isEmpty()) {
            return Collections.emptyList();
        }

        Map<UUID, ColorResponse> colorMap = new LinkedHashMap<>();

        for (ProductVariant variant : variants) {
            if (variant.getColor() == null) continue;

            // --- COLOR ---
            ColorResponse colorResp = colorMap.computeIfAbsent(
                    variant.getColor().getId(),
                    id -> ColorResponse.builder()
                            .id(variant.getColor().getId())
                            .name(variant.getColor().getName())
                            .hex(variant.getColor().getHex())
                            .price(variant.getColor().getPrice())
                            .images(
                                    variant.getColor().getImages().stream()
                                            .map(img -> convertToFileResponse(img.getImage()))
                                            .collect(Collectors.toList())
                            )
                            .chipResponses(new ArrayList<>())
                            .simTypeResponses(new ArrayList<>())
                            .tableModuleResponses(new ArrayList<>())
                            .strapSizeResponses(new ArrayList<>())
                            .build()
            );

            // --- LAPTOP SPEC (existing logic) ---
            if (variant.getLaptopSpec() != null && variant.getLaptopSpec().getChip() != null) {
                handleLaptopSpec(variant, colorResp);
            }

            // --- PHONE SPEC ---
            if (variant.getPhoneSpec() != null && variant.getPhoneSpec().getSimType() != null) {
                handlePhoneSpec(variant, colorResp);
            }

            // --- TABLE SPEC ---
            if (variant.getTableSpec() != null && variant.getTableSpec().getTableModule() != null) {
                handleTableSpec(variant, colorResp);
            }

            // --- WATCH SPEC ---
            if (variant.getWatchSpec() != null && variant.getWatchSpec().getStrapSize() != null) {
                handleWatchSpec(variant, colorResp);
            }
        }

        return new ArrayList<>(colorMap.values());
    }

    private void handleLaptopSpec(ProductVariant variant, ColorResponse colorResp) {
        Chip chip = variant.getLaptopSpec().getChip();
        ChipResponse chipResp = colorResp.getChipResponses().stream()
                .filter(c -> Objects.equals(c.getId(), chip.getId()))
                .findFirst()
                .orElseGet(() -> {
                    ChipResponse newChip = ChipResponse.builder()
                            .id(chip.getId())
                            .name(chip.getName())
                            .ssdResponses(new ArrayList<>())
                            .build();
                    colorResp.getChipResponses().add(newChip);
                    return newChip;
                });

        // --- SSD ---
        Ssd ssd = variant.getLaptopSpec().getSsd();
        if (ssd == null) return;

        SsdResponse ssdResp = chipResp.getSsdResponses().stream()
                .filter(s -> Objects.equals(s.getId(), ssd.getId()))
                .findFirst()
                .orElseGet(() -> {
                    SsdResponse newSsd = SsdResponse.builder()
                            .id(ssd.getId())
                            .name(ssd.getName())
                            .rams(new ArrayList<>())
                            .build();
                    chipResp.getSsdResponses().add(newSsd);
                    return newSsd;
                });

        // --- RAM ---
        Ram ram = variant.getLaptopSpec().getRam();
        if (ram != null && ssdResp.getRams().stream().noneMatch(r -> Objects.equals(r.getId(), ram.getId()))) {
            ssdResp.getRams().add(
                    RamResponse.builder()
                            .id(ram.getId())
                            .name(ram.getName())
                            .price(ram.getPrice())
                            .productVariantId(variant.getId())
                            .build()
            );
        }
    }

    private void handlePhoneSpec(ProductVariant variant, ColorResponse colorResp) {
        SimType simType = variant.getPhoneSpec().getSimType();
        SimTypeResponse simTypeResp = colorResp.getSimTypeResponses().stream()
                .filter(st -> Objects.equals(st.getId(), simType.getId()))
                .findFirst()
                .orElseGet(() -> {
                    SimTypeResponse newSimType = SimTypeResponse.builder()
                            .id(simType.getId())
                            .name(simType.getName())
                            .phoneMemoryResponses(new ArrayList<>())
                            .build();
                    colorResp.getSimTypeResponses().add(newSimType);
                    return newSimType;
                });

        // --- PHONE_MEMORY ---
        PhoneMemory phoneMemory = variant.getPhoneSpec().getPhoneMemory();
        if (phoneMemory != null && simTypeResp.getPhoneMemoryResponses().stream()
                .noneMatch(pm -> Objects.equals(pm.getId(), phoneMemory.getId()))) {
            simTypeResp.getPhoneMemoryResponses().add(
                    PhoneMemoryResponse.builder()
                            .id(phoneMemory.getId())
                            .name(phoneMemory.getName())
                            .price(phoneMemory.getPrice())
                            .productVariantId(variant.getId())
                            .build()
            );
        }
    }

    private void handleTableSpec(ProductVariant variant, ColorResponse colorResp) {
        TableModule tableModule = variant.getTableSpec().getTableModule();
        TableModuleResponse moduleResp = colorResp.getTableModuleResponses().stream()
                .filter(tm -> Objects.equals(tm.getId(), tableModule.getId()))
                .findFirst()
                .orElseGet(() -> {
                    TableModuleResponse newModule = TableModuleResponse.builder()
                            .id(tableModule.getId())
                            .name(tableModule.getName())
                            .tableMemoryResponses(new ArrayList<>())
                            .build();
                    colorResp.getTableModuleResponses().add(newModule);
                    return newModule;
                });

        // --- TABLE_MEMORY ---
        TableMemory tableMemory = variant.getTableSpec().getTableMemory();
        if (tableMemory != null && moduleResp.getTableMemoryResponses().stream()
                .noneMatch(tm -> Objects.equals(tm.getId(), tableMemory.getId()))) {
            moduleResp.getTableMemoryResponses().add(
                    TableMemoryResponse.builder()
                            .id(tableMemory.getId())
                            .name(tableMemory.getName())
                            .price(tableMemory.getPrice())
                            .productVariantId(variant.getId())
                            .build()
            );
        }
    }

    private void handleWatchSpec(ProductVariant variant, ColorResponse colorResp) {
        StrapSize strapSize = variant.getWatchSpec().getStrapSize();
        StrapSizeResponse strapSizeResp = colorResp.getStrapSizeResponses().stream()
                .filter(ss -> Objects.equals(ss.getId(), strapSize.getId()))
                .findFirst()
                .orElseGet(() -> {
                    StrapSizeResponse newStrapSize = StrapSizeResponse.builder()
                            .id(strapSize.getId())
                            .name(strapSize.getName())
                            .dials(new ArrayList<>())
                            .build();
                    colorResp.getStrapSizeResponses().add(newStrapSize);
                    return newStrapSize;
                });

        // --- DIAL ---
        Dial dial = variant.getWatchSpec().getDial();
        if (dial != null && strapSizeResp.getDials().stream()
                .noneMatch(d -> Objects.equals(d.getId(), dial.getId()))) {
            strapSizeResp.getDials().add(
                    DialResponse.builder()
                            .id(dial.getId())
                            .name(dial.getName())
                            .price(dial.getPrice())
                            .productVariantId(variant.getId())
                            .build()
            );
        }
    }
}