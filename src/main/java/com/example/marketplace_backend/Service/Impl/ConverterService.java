package com.example.marketplace_backend.Service.Impl;

import com.example.marketplace_backend.DTO.Responses.models.*;
import com.example.marketplace_backend.Model.*;
import com.example.marketplace_backend.Model.Intermediate_objects.BrandImage;
import com.example.marketplace_backend.Model.Intermediate_objects.CategoryIcon;
import com.example.marketplace_backend.Model.Intermediate_objects.CategoryImage;
import com.example.marketplace_backend.Model.Intermediate_objects.OrderItem;
import com.example.marketplace_backend.Model.Intermediate_objects.OrderStatuses;
import com.example.marketplace_backend.Model.Intermediate_objects.ProductColorImage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ConverterService {
    private final ProductParametersServiceImpl productParametersService;

    @Value("${app.base-url}")
    private String baseUrl;

    public ProductResponse convertToProductResponse(Product product) {
        ProductResponse response = new ProductResponse();
        response.setId(product.getId());
        response.setName(product.getName());
        response.setPrice(product.getPrice());
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

        if (product.getColors() != null && !product.getColors().isEmpty()) {
            List<ColorResponse> colors = product.getColors().stream()
                    .map(productColor -> {
                        ColorResponse colorResponse = new ColorResponse();
                        colorResponse.setName(productColor.getName());
                        colorResponse.setHex(productColor.getHex());

                        // Обработка изображений для каждого цвета
                        if (productColor.getImages() != null && !productColor.getImages().isEmpty()) {
                            List<FileResponse> colorImages = productColor.getImages().stream()
                                    .map(productColorImage -> {
                                        FileEntity image = productColorImage.getImage();
                                        FileResponse fileResponse = new FileResponse();
                                        fileResponse.setUniqueName(image.getUniqueName());
                                        fileResponse.setOriginalName(image.getOriginalName());
                                        fileResponse.setUrl(baseUrl + "/uploads/" + image.getUniqueName());
                                        fileResponse.setFileType(image.getFileType());
                                        return fileResponse;
                                    })
                                    .toList();
                            colorResponse.setImages(colorImages);
                        }
                        return colorResponse;
                    })
                    .toList();
            response.setColors(colors);
        }

        // Собираем все изображения из всех цветов в один список
        if (product.getColors() != null && !product.getColors().isEmpty()) {
            List<FileResponse> allImages = product.getColors().stream()
                    .filter(color -> color.getImages() != null)
                    .flatMap(color -> color.getImages().stream())
                    .map(productColorImage -> {
                        FileEntity image = productColorImage.getImage();
                        FileResponse fileResponse = new FileResponse();
                        fileResponse.setUniqueName(image.getUniqueName());
                        fileResponse.setOriginalName(image.getOriginalName());
                        fileResponse.setUrl(baseUrl + "/uploads/" + image.getUniqueName());
                        fileResponse.setFileType(image.getFileType());
                        return fileResponse;
                    })
                    .toList();
            response.setImages(allImages);
        }

        // Обработка статусов
        if (product.getProductStatuses() != null && !product.getProductStatuses().isEmpty()) {
            List<StatusResponse> statuses = product.getProductStatuses().stream()
                    .map(productStatus -> {
                        Statuses status = productStatus.getStatus();
                        return new StatusResponse(
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

        return response;
    }

    public UserResponse convertToUserResponse(User user) {
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setEmail(user.getEmail());
        response.setName(user.getName());
        response.setPhoneNumber(user.getPhoneNumber());
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

    public OrderResponse convertToOrderResponse(Order order) {
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
                .orderItems(convertOrderItems(order.getOrderItems()))
                .statuses(convertStatuses(order.getOrderStatuses()))
                .build();
    }

    private List<OrderItemResponse> convertOrderItems(List<OrderItem> items) {
        if (items == null) return List.of();

        return items.stream().map(item -> {
            OrderItemResponse response = new OrderItemResponse();
            response.setProductId(item.getProduct().getId());
            response.setProductName(item.getProduct().getName());
            response.setQuantity(item.getQuantity());
            response.setPrice(item.getPrice());
            return response;
        }).collect(Collectors.toList());
    }

    private List<OrderStatusResponse> convertStatuses(List<OrderStatuses> statuses) {
        if (statuses == null) return List.of();

        return statuses.stream().map(status -> {
            OrderStatusResponse response = new OrderStatusResponse();
            response.setName(status.getStatus().getName());
            response.setPrimaryColor(status.getStatus().getPrimaryColor());
            response.setBackgroundColor(status.getStatus().getBackgroundColor());
            return response;
        }).collect(Collectors.toList());
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
}