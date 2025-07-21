package com.example.marketplace_backend.controller.admin;

import com.example.marketplace_backend.DTO.Requests.models.BrandRequest;
import com.example.marketplace_backend.DTO.Responses.models.BrandResponse;
import com.example.marketplace_backend.Model.Brand;
import com.example.marketplace_backend.Service.Impl.BrandServiceImpl;
import com.example.marketplace_backend.Service.Impl.ConverterService;
import com.example.marketplace_backend.Service.Impl.FileUploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/brands")
public class AdminBrandController {
    private final BrandServiceImpl brandService;
    private final FileUploadService fileUploadService;
    private final ConverterService converterService;

    @GetMapping("/list")
    public ResponseEntity<Page<Brand>> getPaginatedBrands(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Brand> brands = brandService.findAll(pageable);
        return ResponseEntity.ok(brands);
    }

    @GetMapping("/inactive")
    public ResponseEntity<Page<Brand>> getInactiveBrands(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Brand> brands = brandService.findAllDeActive(pageable);
        return ResponseEntity.ok(brands);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Brand> getBrandById(@PathVariable UUID id) {
        Optional<Brand> brand = brandService.findById(id);
        return brand.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Long>> getBrandsStats() {
        Map<String, Long> stats = new HashMap<>();
        stats.put("activeBrands", brandService.countActiveBrands());
        stats.put("inactiveBrands", brandService.countDeActiveBrands());
        return ResponseEntity.ok(stats);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> softDeleteBrand(@PathVariable UUID id) {
        try {
            brandService.softDelete(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/restore/{id}")
    public ResponseEntity<Brand> restoreBrand(@PathVariable UUID id) {
        try {
            brandService.restore(id);
            Optional<Brand> brand = brandService.findById(id);
            return brand.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping(value = "/create", consumes = {"multipart/form-data"})
    public ResponseEntity<?> createBrandWithImages(@ModelAttribute BrandRequest request) throws Exception {
        if (request.getImage() == null || request.getImage().isEmpty()) {
            return ResponseEntity.badRequest().body("Изображение обязательно для создания бренда.");
        }

        BrandResponse brand = converterService.convertToBrandResponse(brandService.createBrand(request));
        return ResponseEntity.ok(brand);
    }


    @DeleteMapping("/{id}/permanent")
    public ResponseEntity<Void> permanentDeleteBrand(@PathVariable UUID id) {
        try {
            brandService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/purge")
    public ResponseEntity<Void> purgeOldBrands() {
        try {
            brandService.purgeOldBrands();
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping(value = "/edit/{id}", consumes = {"multipart/form-data"})
    public ResponseEntity<BrandResponse> editBrand(
            @PathVariable UUID id,
            @ModelAttribute BrandRequest request
    ) throws IOException {
        BrandResponse brandResponse = converterService.convertToBrandResponse(brandService.editBrand(id, request));
        return ResponseEntity.ok(brandResponse);
    }

    /*
     * try {
     *     Optional<Brand> brandOpt = brandService.findById(brandId);
     *     if (brandOpt.isEmpty()) {
     *         return ResponseEntity.notFound().build();
     *     }
     *     Brand brand = brandOpt.get();
     *     if (brand.getBrandImages() != null) {
     *         brand.getBrandImages().removeIf(img ->
     *                 img.getImage() != null && img.getImage().getId().equals(imageId)
     *         );
     *         brandService.save(brand);
     *     }
     *     return ResponseEntity.noContent().build();
     * } catch (Exception e) {
     *     return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
     * }
     */
    @DeleteMapping("/{brandId}/image") // ИЗМЕНЕНО: path с /images/{imageId} на /image
    public ResponseEntity<Void> deleteBrandImage(@PathVariable UUID brandId) {
        try {
            brandService.deleteBrandImage(brandId);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}