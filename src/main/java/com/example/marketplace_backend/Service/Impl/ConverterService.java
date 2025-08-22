package com.example.marketplace_backend.Service.Impl;

import com.example.marketplace_backend.DTO.Responses.models.*;
import com.example.marketplace_backend.Model.*;
import com.example.marketplace_backend.Model.Intermediate_objects.*;
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
        ProductResponse response = new ProductResponse();
        response.setId(product.getId());
        response.setName(product.getName());
        response.setPrice(product.getPrice());
        response.setPriceDescription(product.getPriceDescription());
        response.setAvailability(product.isAvailability());
        response.setTitle(product.getTitle());
        response.setDescription(product.getDescription());
        response.setCreatedAt(product.getCreatedAt());
        response.setUpdatedAt(product.getUpdatedAt());
        response.setDeletedAt(product.getDeletedAt());

        // Обработка подкатегории
        if (product.getSubcategory() != null) {
            response.setSubcategoryId(product.getSubcategory().getId());
            response.setSubcategoryName(product.getSubcategory().getName());

            if (product.getSubcategory().getCategory() != null) {
                response.setCategoryId(product.getSubcategory().getCategory().getId());
                response.setCategoryName(product.getSubcategory().getCategory().getName());
            }
        }

        if (product.getDiscountedPrice() != null) {
            response.setDiscountedPrice(product.getDiscountedPrice());
        }

        if (product.getBrand() != null) {
            response.setBrandId(product.getBrand().getId());
            response.setBrandName(product.getBrand().getName());
        }

        // Обработка цветов с памятью и типами подключения
        if (product.getColors() != null && !product.getColors().isEmpty()) {
            List<ColorResponse> colors = product.getColors().stream()
                    .map(this::convertToColorResponse)
                    .toList();
            response.setColors(colors);
        }

        // Собираем color images
        List<FileResponse> colorImages = product.getColors() != null
                ? product.getColors().stream()
                .filter(color -> color.getImages() != null && !color.getImages().isEmpty())
                .flatMap(color -> color.getImages().stream())
                .map(productColorImage -> {
                    FileEntity image = productColorImage.getImage();
                    return FileResponse.builder()
                            .uniqueName(image.getUniqueName())
                            .originalName(image.getOriginalName())
                            .url(baseUrl + "/uploads/" + image.getUniqueName())
                            .fileType(image.getFileType())
                            .build();
                })
                .toList()
                : List.of();

        // Собираем product images
        List<FileResponse> productImages = product.getImages() != null
                ? product.getImages().stream()
                .map(productImage -> {
                    FileEntity image = productImage.getImage();
                    return FileResponse.builder()
                            .uniqueName(image.getUniqueName())
                            .originalName(image.getOriginalName())
                            .url(baseUrl + "/uploads/" + image.getUniqueName())
                            .fileType(image.getFileType())
                            .build();
                })
                .toList()
                : List.of();

        // Логика выбора
        if (!colorImages.isEmpty()) {
            response.setImages(colorImages); // приоритет цветных фото
        } else {
            response.setImages(productImages); // fallback — общие фото
        }

        // Обработка статусов
        if (product.getProductStatuses() != null && !product.getProductStatuses().isEmpty()) {
            List<StatusResponse> statuses = product.getProductStatuses().stream()
                    .map(productStatus -> {
                        Statuses status = productStatus.getStatus();
                        return new StatusResponse(
                                status.getId(),
                                status.getName(),
                                status.getPrimaryColor(),
                                status.getBackgroundColor()
                        );
                    })
                    .toList();
            response.setStatuses(statuses);
        }

        // Добавление параметров
        List<ProductParameterResponse> parameterResponses = productParametersService
                .getParametersWithSubParams(product.getId())
                .stream()
                .map(param -> {
                    ProductParameterResponse paramResponse = new ProductParameterResponse();
                    paramResponse.setId(param.getId());
                    paramResponse.setName(param.getName());

                    List<ProductSubParameterResponse> subParamResponses = param.getProductSubParameters().stream()
                            .map(sub -> {
                                ProductSubParameterResponse subResponse = new ProductSubParameterResponse();
                                subResponse.setId(sub.getId());
                                subResponse.setName(sub.getName());
                                subResponse.setValue(sub.getValue());
                                return subResponse;
                            }).toList();

                    paramResponse.setSubParameters(subParamResponses);
                    return paramResponse;
                })
                .toList();

        response.setParameters(parameterResponses);

        return response;
    }

    public ColorResponse convertToColorResponse(ProductColor productColor) {
        ColorResponse response = new ColorResponse();
        response.setId(productColor.getId());
        response.setName(productColor.getName());
        response.setHex(productColor.getHex());

        // Обработка спецификаций телефонов
        if (productColor.getPhoneSpecs() != null && !productColor.getPhoneSpecs().isEmpty()) {
            List<PhoneSpecResponse> phoneSpecs = productColor.getPhoneSpecs().stream()
                    .map(phoneSpec -> PhoneSpecResponse.builder()
                            .id(phoneSpec.getId())
                            .memory(phoneSpec.getMemory())
                            .price(phoneSpec.getPrice())
                            .simType(phoneSpec.getSimType())
                            .build())
                    .toList();
            response.setPhoneSpecs(phoneSpecs);
        }

        // Обработка спецификаций ноутбуков
        if (productColor.getLaptopSpecs() != null && !productColor.getLaptopSpecs().isEmpty()) {
            List<LaptopSpecResponse> laptopSpecs = productColor.getLaptopSpecs().stream()
                    .map(laptopSpec -> LaptopSpecResponse.builder()
                            .id(laptopSpec.getId())
                            .ssdMemory(laptopSpec.getSsdMemory())
                            .price(laptopSpec.getPrice())
                            .build())
                    .toList();
            response.setLaptopSpecs(laptopSpecs);
        }

        // Обработка изображений цвета
        List<FileResponse> images = new ArrayList<>();
        if (productColor.getImages() != null && !productColor.getImages().isEmpty()) {
            images = productColor.getImages().stream()
                    .map(colorImage -> {
                        FileEntity image = colorImage.getImage();
                        FileResponse fileResponse = new FileResponse();
                        fileResponse.setOriginalName(image.getOriginalName());
                        fileResponse.setUniqueName(image.getUniqueName());
                        fileResponse.setFileType(image.getFileType());
                        fileResponse.setUrl(baseUrl + "/uploads/" + image.getUniqueName());
                        return fileResponse;
                    })
                    .toList();
        }
        response.setImages(images);

        return response;
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

        // Обработка изображения категории
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

        // Обработка иконки категории
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
                    .toList();
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
                    .toList();
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

    // Обновленный метод для работы с единственным статусом
    public OrderResponse convertToOrderResponse(Order order) {
        return OrderResponse.builder()
                .id(order.getId())
                .address(order.getAddress())
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
            response.setProductResponse(convertToProductResponse(item.getProduct()));
            response.setQuantity(item.getQuantity());
            response.setPrice(item.getPrice());
            return response;
        }).collect(Collectors.toList());
    }

    // Обновленный метод для работы с единственным статусом
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
                    .toList();
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
                    .productResponse(convertToProductResponse(item.getProduct()))
                    .quantity(item.getQuantity())
                    .pricePerItem(item.getPrice())
                    .totalPrice(totalPrice)
                    .favoriteItemId(item.getId())
                    .addedAt(item.getAddedAt())
                    .build();
        }).toList();

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
                .productResponse(convertToProductResponse(item.getProduct()))
                .build();
    }

    public CartResponse convertToCartResponse(Cart cart) {
        List<CartItemResponse> items = cart.getCartItems().stream().map(item -> {
            BigDecimal totalPrice = item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
            return CartItemResponse.builder()
                    .cartItemId(item.getId())
                    .quantity(item.getQuantity())
                    .pricePerItem(item.getPrice())
                    .totalPrice(totalPrice)
                    .addedAt(item.getCreatedAt())
                    .productResponse(convertToProductResponse(item.getProduct()))
                    .build();
        }).toList();

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
                .productResponse(convertToProductResponse(item.getProduct()))
                .build();
    }

    public OrderItemResponse toOrderItemResponse(OrderItem item) {
        return OrderItemResponse.builder()
                .id(item.getId())
                .productResponse(convertToProductResponse(item.getProduct()))
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
                                .toList()
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
}