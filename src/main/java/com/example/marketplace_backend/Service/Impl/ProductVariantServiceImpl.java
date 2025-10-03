package com.example.marketplace_backend.Service.Impl;

import com.example.marketplace_backend.Model.ProductVariant;
import com.example.marketplace_backend.Repositories.ProductVariantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductVariantServiceImpl {
    private final ProductVariantRepository productVariantRepository;

    @Transactional
    public void deleteProductVariant(UUID productVariantId) {
        if (!productVariantRepository.existsById(productVariantId)) {
            throw new RuntimeException("ProductVariant not found with id: " + productVariantId);
        }
        productVariantRepository.deleteById(productVariantId);
        System.out.println("Delete ProductVariant with id: " + productVariantId);
    }

    @Transactional
    public void softDeleteProductVariant(UUID productVariantId) {
        if (!productVariantRepository.existsById(productVariantId)) {
            throw new RuntimeException("ProductVariant not found with id: " + productVariantId);
        }
        productVariantRepository.softDeleteById(productVariantId, LocalDateTime.now());
    }

    @Transactional
    public void restoreProductVariant(UUID productVariantId) {
        if (!productVariantRepository.existsById(productVariantId)) {
            throw new RuntimeException("ProductVariant not found with id: " + productVariantId);
        }
        productVariantRepository.softDeleteById(productVariantId, null);
    }

    @Transactional
    public void purgeProductVariant(UUID productVariantId) {
        ProductVariant variant = productVariantRepository.findById(productVariantId)
                .orElseThrow(() -> new RuntimeException("ProductVariant not found with id: " + productVariantId));

        if (variant.getDeletedAt() == null) {
            throw new RuntimeException("ProductVariant is not soft-deleted, cannot purge");
        }

        productVariantRepository.deleteById(productVariantId);
    }

    @Transactional
    public void purgeAllProductVariants() {
        productVariantRepository.deleteAllSoftDeleted();
    }
}