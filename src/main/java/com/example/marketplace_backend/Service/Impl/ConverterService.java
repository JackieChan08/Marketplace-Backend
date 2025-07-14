package com.example.marketplace_backend.Service.Impl;

import com.example.marketplace_backend.DTO.Responses.models.*;
import com.example.marketplace_backend.Model.*;
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
        response.setDescriptions(product.getDescriptions());

        Subcategory subcategory = product.getSubcategory();
        if (subcategory != null) {
            response.setSubcategoryId(subcategory.getId());
            response.setSubcategoryName(subcategory.getName());

            if (subcategory.getCategory() != null) {
                response.setCategoryId(subcategory.getCategory().getId());
                response.setCategoryName(subcategory.getCategory().getName());
            }
        }
        if (product.getPrice() != null) {
            response.setPrice(product.getPrice());
        }
        if (product.getDiscountedPrice() != null) {
            response.setDiscountedPrice(product.getDiscountedPrice());
        }

        if (product.getBrand() != null) {
            response.setBrandId(product.getBrand().getId());
        }

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

        if (subcategory.getProducts() != null && !subcategory.getProducts().isEmpty()) {
            List<ProductResponse> productResponses = subcategory.getProducts().stream()
                    .filter(product -> product.getDeletedAt() == null)
                    .map(this::convertToProductResponse)
                    .toList();
            response.setProducts(productResponses);
        }
        if (subcategory.getSubcategoryImages() != null && !subcategory.getSubcategoryImages().isEmpty()) {
            List<FileResponse> imageFiles = subcategory.getSubcategoryImages().stream()
                    .map(subcategoryImage -> {
                        FileEntity image = subcategoryImage.getImage();
                        FileResponse fileResponse = new FileResponse();
                        fileResponse.setUniqueName(image.getUniqueName());
                        fileResponse.setOriginalName(image.getOriginalName());
                        fileResponse.setUrl(baseUrl + "/uploads/" + image.getUniqueName());
                        fileResponse.setFileType(image.getFileType());
                        return fileResponse;
                    })
                    .toList();

            response.setImageFiles(imageFiles);
        }

        return response;
    }


    public CategoryResponse convertToCategoryResponse(Category category) {
        CategoryResponse response = new CategoryResponse();
        response.setId(category.getId());
        response.setName(category.getName());

        if (category.getCategoryImages() != null && !category.getCategoryImages().isEmpty()) {
            List<FileResponse> imageFiles = category.getCategoryImages().stream()
                    .map(categoryImage -> {
                        FileEntity image = categoryImage.getImage();
                        FileResponse fileResponse = new FileResponse();
                        fileResponse.setUniqueName(image.getUniqueName());
                        fileResponse.setOriginalName(image.getOriginalName());
                        fileResponse.setUrl(baseUrl + "/uploads/" + image.getUniqueName());
                        fileResponse.setFileType(image.getFileType());
                        return fileResponse;
                    })
                    .toList();

            response.setImageFiles(imageFiles);
        }

        List<ProductResponse> productResponses = category.getSubcategories().stream()
                .filter(subcategory -> subcategory.getDeletedAt() == null)
                .flatMap(subcategory -> subcategory.getProducts().stream())
                .filter(product -> product.getDeletedAt() == null)
                .map(this::convertToProductResponse)
                .toList();

        response.setProducts(productResponses);

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

}
