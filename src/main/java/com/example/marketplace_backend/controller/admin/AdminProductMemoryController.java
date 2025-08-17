package com.example.marketplace_backend.controller.admin;

import com.example.marketplace_backend.DTO.Requests.models.ProductMemoryRequest;
import com.example.marketplace_backend.DTO.Responses.models.ProductMemoryResponse;
import com.example.marketplace_backend.Repositories.ProductMemoryRepository;
import com.example.marketplace_backend.Service.Impl.ProductMemoryServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/admin/memories")
@RequiredArgsConstructor
public class AdminProductMemoryController {
    private final ProductMemoryRepository productMemoryRepository;
    private final ProductMemoryServiceImpl productMemoryService;

    @PostMapping("/create")
    public ResponseEntity<ProductMemoryResponse> create(
            @ModelAttribute ProductMemoryRequest productMemoryRequest
    ) {
        return ResponseEntity.ok(productMemoryService.createProductMemory(productMemoryRequest));
    }

    @PostMapping("/update/{id}")
    public ResponseEntity<ProductMemoryResponse> update(
            @PathVariable UUID id,
            @ModelAttribute ProductMemoryRequest productMemoryRequest
    ) {
        return ResponseEntity.ok(productMemoryService.updateProductMemory(id, productMemoryRequest));
    }

    @GetMapping
    public ResponseEntity<List<ProductMemoryResponse>> getAll() {
        return ResponseEntity.ok(productMemoryService.getAllProductMemory());
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> delete(
            @PathVariable UUID id
    ) {
        productMemoryService.deleteProductMemory(id);
        return ResponseEntity.ok().build();
    }

}
