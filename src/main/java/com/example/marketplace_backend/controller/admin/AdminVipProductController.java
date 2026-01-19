package com.example.marketplace_backend.controller.admin;

import com.example.marketplace_backend.DTO.Requests.models.VipProductRequest;
import com.example.marketplace_backend.DTO.Responses.models.VipProductResponse;
import com.example.marketplace_backend.Service.Impl.VipProductServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/vip-products")
@Slf4j
public class AdminVipProductController {
    private final VipProductServiceImpl vipProductService;

    @PostMapping(value = "/create", consumes = "multipart/form-data")
    public ResponseEntity<VipProductResponse> createVipProduct(
            @ModelAttribute VipProductRequest vipProductRequest) throws IOException {
        log.info("Запрос на создание VIP продукта: {}", vipProductRequest.getName());
        try {
            return vipProductService.createVipProduct(vipProductRequest);
        } catch (Exception e) {
            log.error("Ошибка при создании VIP продукта", e);
            throw e;
        }
    }

    @PostMapping(value = "/edit/{id}", consumes = "multipart/form-data")
    public ResponseEntity<VipProductResponse> editVipProduct(
            @PathVariable UUID id,
            @ModelAttribute VipProductRequest vipProductRequest) throws IOException {
        log.info("Запрос на обновление VIP продукта с ID: {}", id);
        try {
            return vipProductService.updateVipProduct(id, vipProductRequest);
        } catch (Exception e) {
            log.error("Ошибка при обновлении VIP продукта с ID: {}", id, e);
            throw e;
        }
    }

    @GetMapping("")
    public ResponseEntity<List<VipProductResponse>> getAllVipProducts() {
        log.info("Запрос на получение всех VIP продуктов (админ)");
        return vipProductService.getAllVipProducts();
    }

    @DeleteMapping("/delete")
    public ResponseEntity<Void> deleteVipProduct(@RequestParam UUID vipProductId) {
        log.info("Запрос на удаление VIP продукта с ID: {}", vipProductId);
        try {
            vipProductService.deleteVipProduct(vipProductId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Ошибка при удалении VIP продукта с ID: {}", vipProductId, e);
            throw e;
        }
    }
}