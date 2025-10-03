package com.example.marketplace_backend.controller.admin;

import com.example.marketplace_backend.Service.Impl.ProductVariantServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/admin/product-variants")
public class AdminProductVariantController {
    private final ProductVariantServiceImpl productVariantService;

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> softDeleteProductVariant(@PathVariable UUID id) {
        productVariantService.softDeleteProductVariant(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PostMapping("/restore/{id}")
    public ResponseEntity<Void> restoreProductVariant(@PathVariable UUID id) {
        productVariantService.restoreProductVariant(id);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @DeleteMapping("/{id}/permanent")
    public ResponseEntity<Void> permanentDeleteProductVariant(@PathVariable UUID id) {
        productVariantService.deleteProductVariant(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @DeleteMapping("/purge")
    public ResponseEntity<Void> purgeProductVariant(@RequestBody UUID id) {
        productVariantService.purgeProductVariant(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @DeleteMapping("/purge-all")
    public ResponseEntity<Void> purgeAllProductVariants() {
        productVariantService.purgeAllProductVariants();
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}