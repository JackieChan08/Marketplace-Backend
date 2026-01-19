package com.example.marketplace_backend.controller;

import com.example.marketplace_backend.DTO.Responses.models.VipProductResponse;
import com.example.marketplace_backend.Service.Impl.VipProductServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/vip-products")
@RequiredArgsConstructor
@Slf4j
public class VipProductController {
    private final VipProductServiceImpl vipProductService;

    @GetMapping()
    public ResponseEntity<List<VipProductResponse>> getAllVipProducts() {
        log.info("Запрос на получение всех VIP продуктов (публичный)");
        return vipProductService.getAllVipProducts();
    }

    @GetMapping("/{id}")
    public ResponseEntity<VipProductResponse> getVipProductById(@PathVariable UUID id) {
        log.info("Запрос на получение VIP продукта по ID: {}", id);
        return ResponseEntity.ok(vipProductService.getVipProductById(id));
    }
}
