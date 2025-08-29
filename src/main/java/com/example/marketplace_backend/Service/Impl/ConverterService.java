package com.example.marketplace_backend.Service.Impl;

import com.example.marketplace_backend.DTO.Responses.models.*;
import com.example.marketplace_backend.Model.*;
import com.example.marketplace_backend.Model.Intermediate_objects.*;
import com.example.marketplace_backend.Model.ProductSpec.LaptopSpec;
import com.example.marketplace_backend.Model.ProductSpec.PhoneSpec;
import com.example.marketplace_backend.Repositories.ProductColorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ConverterService {
    private final ProductParametersServiceImpl productParametersService;
    private final ProductColorRepository productColorRepository;

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

        // Variants
        List<VariantResponse> variantResponses = new ArrayList<>();
        if (product.getVariants() != null) {
            for (ProductVariant variant : product.getVariants()) {
                VariantResponse vr = VariantResponse.builder()
                        .id(variant.getId())
                        .color(variant.getColor() != null ? convertToColorResponse(variant.getColor()) : null)
                        .phoneSpec(variant.getPhoneSpec() != null ? convertToPhoneSpecResponse(variant.getPhoneSpec()) : null)
                        .laptopSpec(variant.getLaptopSpec() != null ? convertToLaptopSpecResponse(variant.getLaptopSpec()) : null)
                        .build();

                variantResponses.add(vr);
            }
        }
        response.setVariants(variantResponses);

        // Images: приоритет — изображения из вариантов (цветов), иначе общие product images
        List<FileResponse> variantImages = variantResponses.stream()
                .filter(v -> v.getColor() != null && v.getColor().getImages() != null)
                .flatMap(v -> v.getColor().getImages().stream())
                .collect(Collectors.toList());

        List<FileResponse> productImages = (product.getImages() != null)
                ? product.getImages().stream()
                .map(img -> toFileResponse(img.getImage()))
                .collect(Collectors.toList())
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

    private ColorResponse convertToColorResponse(ProductColor color) {
        if (color == null) return null;

        List<FileResponse> images = (color.getImages() != null)
                ? color.getImages().stream()
                // предполагается, что img.getImage() возвращает FileEntity
                .map(img -> toFileResponse(img.getImage()))
                .collect(Collectors.toList())
                : List.of();

        return ColorResponse.builder()
                .id(color.getId())
                .name(color.getName())
                .hex(color.getHex())
                .images(images)
                .build();
    }

    private PhoneSpecResponse convertToPhoneSpecResponse(PhoneSpec spec) {
        if (spec == null) return null;
        return PhoneSpecResponse.builder()
                .id(spec.getId())
                .memory(spec.getMemory())
                .price(spec.getPrice())
                .simType(spec.getSimType())
                .build();
    }

    private LaptopSpecResponse convertToLaptopSpecResponse(LaptopSpec spec) {
        if (spec == null) return null;
        return LaptopSpecResponse.builder()
                .id(spec.getId())
                .ssdMemory(spec.getSsdMemory())
                .price(spec.getPrice())
                .build();
    }

    private FileResponse toFileResponse(FileEntity image) {
        if (image == null) return null;
        return FileResponse.builder()
                .uniqueName(image.getUniqueName())
                .originalName(image.getOriginalName())
                .url(baseUrl + "/uploads/" + image.getUniqueName())
                .fileType(image.getFileType())
                .build();
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

            FileEntity image = firstImage.getImage();
            if (image != null) {
                FileResponse fileResponse = new FileResponse();
                fileResponse.setOriginalName(image.getOriginalName());
                fileResponse.setUniqueName(image.getUniqueName());
                fileResponse.setFileType(image.getFileType());
                fileResponse.setUrl(baseUrl + "/uploads/" + image.getUniqueName());

                categoryImageResponse.setId(firstImage.getId());
                categoryImageResponse.setImage(fileResponse);
            }

            response.setCategoryImage(categoryImageResponse);
        }

        // Process category icon
        if (category.getCategoryIcons() != null && !category.getCategoryIcons().isEmpty()) {
            CategoryIcon firstIcon = category.getCategoryIcons().iterator().next();
            CategoryIconResponse categoryIconResponse = new CategoryIconResponse();

            FileEntity icon = firstIcon.getIcon();
            if (icon != null) {
                FileResponse fileResponse = new FileResponse();
                fileResponse.setOriginalName(icon.getOriginalName());
                fileResponse.setUniqueName(icon.getUniqueName());
                fileResponse.setFileType(icon.getFileType());
                fileResponse.setUrl(baseUrl + "/uploads/" + icon.getUniqueName());

                categoryIconResponse.setId(firstIcon.getId());
                categoryIconResponse.setIcon(fileResponse);
            }

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
                    .map(userImage -> {
                        FileEntity image = userImage.getImage();
                        FileResponse fileResponse = new FileResponse();
                        fileResponse.setUniqueName(image.getUniqueName());
                        fileResponse.setOriginalName(image.getOriginalName());
                        fileResponse.setUrl(baseUrl + "/uploads/" + image.getUniqueName());
                        fileResponse.setFileType(image.getFileType());
                        return fileResponse;
                    })
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

            FileEntity image = firstImage.getImage();
            if (image != null) {
                FileResponse fileResponse = new FileResponse();
                fileResponse.setOriginalName(image.getOriginalName());
                fileResponse.setUniqueName(image.getUniqueName());
                fileResponse.setFileType(image.getFileType());
                fileResponse.setUrl(baseUrl + "/uploads/" + image.getUniqueName());

                brandImageResponse.setId(firstImage.getId());
                brandImageResponse.setImage(fileResponse);
            }
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

        FileEntity image = vipProduct.getImage();
        if (image != null) {
            FileResponse fileResponse = new FileResponse();
            fileResponse.setOriginalName(image.getOriginalName());
            fileResponse.setUniqueName(image.getUniqueName());
            fileResponse.setFileType(image.getFileType());
            fileResponse.setUrl(baseUrl + "/uploads/" + image.getUniqueName());

            response.setImage(image);
        }

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
                .phoneSpec(convertToPhoneSpecResponse(variant.getPhoneSpec()))
                .laptopSpec(convertToLaptopSpecResponse(variant.getLaptopSpec()))
                .build();
    }
}