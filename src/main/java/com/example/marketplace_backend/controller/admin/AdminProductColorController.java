package com.example.marketplace_backend.controller.admin;

import com.example.marketplace_backend.DTO.Responses.models.FilteredColorResponseForPhone;
import com.example.marketplace_backend.Service.Impl.ProductColorServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/colors")
public class AdminProductColorController {
    private final ProductColorServiceImpl productFilterService;

    @GetMapping("/{productId}/filter")
    public ResponseEntity<FilteredColorResponseForPhone> filterProduct(
            @PathVariable UUID productId,
            @RequestParam(required = false) UUID colorId,
            @RequestParam(required = false) UUID memoryId,
            @RequestParam(required = false) UUID connectionId
    ) {
        FilteredColorResponseForPhone response = productFilterService.filterProduct(
                productId, colorId, memoryId, connectionId
        );
        return ResponseEntity.ok(response);
    }
}
