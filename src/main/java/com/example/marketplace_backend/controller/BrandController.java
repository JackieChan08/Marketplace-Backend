package com.example.marketplace_backend.controller;

import com.example.marketplace_backend.DTO.Responses.models.BrandResponse;
import com.example.marketplace_backend.Model.Brand;
import com.example.marketplace_backend.Repositories.BrandRepository;
import com.example.marketplace_backend.Service.Impl.BrandServiceImpl;
import com.example.marketplace_backend.Service.Impl.ConverterService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/brands")
@RequiredArgsConstructor
@Tag(name = "Brand Controller", description = "API для управления брендами товаров")
public class BrandController {

    private final BrandServiceImpl brandService;
    private final BrandRepository brandRepository;
    private final ConverterService converter;
    private final BrandServiceImpl brandServiceImpl;

    @Value("${app.base-url}")
    private String baseUrl;

    @GetMapping("/{id}")
    public ResponseEntity<BrandResponse> getById(
            @Parameter(description = "ID бренда", required = true, example = "123")
            @PathVariable UUID id
    ) {
        Brand brand = brandService.getById(id);
        if (brand == null || brand.getDeletedAt() != null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        BrandResponse response = converter.convertToBrandResponse(brand);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<Page<BrandResponse>> getBrands(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);

        Page<BrandResponse> responses = brandService.findAllActive(pageable).map(converter::convertToBrandResponse);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("all")
    public ResponseEntity<?> getAll() {
        try {
            List<BrandResponse> responses = brandService.getAll().stream()
                    .map(converter::convertToBrandResponse)
                    .toList();
            return ResponseEntity.ok(responses);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Ошибка при получении брендов: " + e.getMessage());
        }
    }

}