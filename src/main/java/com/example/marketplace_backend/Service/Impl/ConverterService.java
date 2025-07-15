package com.example.marketplace_backend.Service.Impl;

import com.example.marketplace_backend.DTO.Responses.models.*;
import com.example.marketplace_backend.Model.*;
import com.example.marketplace_backend.Model.Intermediate_objects.BrandImage;
import com.example.marketplace_backend.Model.Intermediate_objects.CategoryImage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ConverterService {

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

        // Обработка подкатегории
        if (product.getSubcategory() != null) {
            response.setSubcategoryId(product.getSubcategory().getId());
            response.setSubcategoryName(product.getSubcategory().getName());

            // Обработка категории через подкатегорию
            if (product.getSubcategory().getCategory() != null) {
                response.setCategoryId(product.getSubcategory().getCategory().getId());
                response.setCategoryName(product.getSubcategory().getCategory().getName());
            }
        }
        if (product.getPrice() != null) {
            response.setPrice(product.getPrice());
        }
        if (product.getDiscountedPrice() != null) {
            response.setDiscountedPrice(product.getDiscountedPrice());
        }

        // Обработка бренда
        if (product.getBrand() != null) {
            response.setBrandId(product.getBrand().getId());
            response.setBrandName(product.getBrand().getName());
        }

        // Обработка изображений
        if (product.getProductImages() != null && !product.getProductImages().isEmpty()) {
            List<FileResponse> images = product.getProductImages().stream()
                    .map(productImage -> {
                        FileEntity image = productImage.getImage();
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


        // Обработка статусов
        if (product.getProductStatuses() != null && !product.getProductStatuses().isEmpty()) {
            List<String> statuses = product.getProductStatuses().stream()
                    .map(productStatus -> productStatus.getStatus().getName())
                    .toList();
            response.setStatuses(statuses);
        }

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

        if (category.getCategoryImages() != null && !category.getCategoryImages().isEmpty()) {
            CategoryImage firstImage = category.getCategoryImages().iterator().next();
            CategoryImageResponse categoryImageResponse = new CategoryImageResponse();

            // предположим, внутри image вложен FileEntity
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
                fileResponse.setUrl(baseUrl + "/uploads/" + image.getUniqueName())
                ;
                brandImageResponse.setId(firstImage.getId());
                brandImageResponse.setImage(fileResponse);
            }
            response.setBrandImage(brandImageResponse);
        }

        return response;
    }

}
