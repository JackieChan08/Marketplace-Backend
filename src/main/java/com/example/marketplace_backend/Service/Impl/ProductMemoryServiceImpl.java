package com.example.marketplace_backend.Service.Impl;

import com.example.marketplace_backend.DTO.Requests.models.ProductMemoryRequest;
import com.example.marketplace_backend.DTO.Responses.models.ProductMemoryResponse;
import com.example.marketplace_backend.Model.Phone.ProductMemory;
import com.example.marketplace_backend.Repositories.ProductMemoryRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.lang.module.FindException;
import java.util.List;
import java.util.UUID;

@Service
public class ProductMemoryServiceImpl {
    private final ProductMemoryRepository productMemoryRepository;
    private final ConverterService converterService;

    public ProductMemoryServiceImpl(ProductMemoryRepository productMemoryRepository,
                                    ConverterService converterService) {
        this.productMemoryRepository = productMemoryRepository;
        this.converterService = converterService;
    }

    public ProductMemoryResponse createProductMemory(ProductMemoryRequest productMemoryRequest) {
        ProductMemory productMemory = ProductMemory.builder()
                .id(productMemoryRequest.getId())
                .memory(productMemoryRequest.getMemory())
                .build();

        productMemoryRepository.save(productMemory);

        return converterService.convertToProductMemoryResponse(productMemory);
    }

    public ProductMemoryResponse updateProductMemory(UUID id, ProductMemoryRequest productMemoryRequest) {
        ProductMemory productMemory = productMemoryRepository.findById(id).orElseThrow(EntityNotFoundException::new);

        productMemory.setMemory(productMemoryRequest.getMemory());
        productMemoryRepository.save(productMemory);

        return converterService.convertToProductMemoryResponse(productMemory);
    }

    public void deleteProductMemory(UUID id) {
        if(!productMemoryRepository.existsById(id)) {
            throw new EntityNotFoundException("Product memory not found: " + id);
        }
        productMemoryRepository.deleteById(id);
    }

    public List<ProductMemoryResponse> getAllProductMemory() {
        return productMemoryRepository.findAll().stream()
                .map(converterService::convertToProductMemoryResponse)
                .toList();
    }
}
