package com.example.marketplace_backend.Service.Impl;

import com.example.marketplace_backend.DTO.Requests.models.VipProductRequest;
import com.example.marketplace_backend.DTO.Responses.models.VipProductResponse;
import com.example.marketplace_backend.Model.FileEntity;
import com.example.marketplace_backend.Model.VipProduct;
import com.example.marketplace_backend.Repositories.VipProductRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
public class VipProductServiceImpl extends BaseServiceImpl<VipProduct, UUID> {
    private final VipProductRepository vipProductRepository;
    private final FileUploadService fileUploadService;
    private final ConverterService converterService;

    public VipProductServiceImpl(VipProductRepository vipProductRepository,
                                 FileUploadService fileUploadService,
                                 ConverterService converterService) {
        super(vipProductRepository);
        this.vipProductRepository = vipProductRepository;
        this.fileUploadService = fileUploadService;
        this.converterService = converterService;
    }

    public ResponseEntity<VipProductResponse> createVipProduct(VipProductRequest vipProductRequest) {
        // Валидация входных данных
        if (vipProductRequest == null) {
            throw new IllegalArgumentException("VipProductRequest не может быть null");
        }
        if (vipProductRequest.getName() == null || vipProductRequest.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Название продукта обязательно");
        }

        log.info("Создание VIP продукта: {}", vipProductRequest.getName());

        try {
            VipProduct vipProduct = new VipProduct();
            vipProduct.setName(vipProductRequest.getName().trim());

            // Сохранение изображения, если оно предоставлено
            if (vipProductRequest.getImage() != null && !vipProductRequest.getImage().isEmpty()) {
                FileEntity savedImage = fileUploadService.saveImage(vipProductRequest.getImage());
                vipProduct.setImage(savedImage);
            }

            vipProduct = vipProductRepository.save(vipProduct);
            log.info("VIP продукт успешно создан с ID: {}", vipProduct.getId());

            VipProductResponse vipProductResponse = converterService.convertToVipProductResponse(vipProduct);
            return ResponseEntity.ok(vipProductResponse);

        } catch (IOException e) {
            log.error("Ошибка при сохранении изображения для VIP продукта: {}", vipProductRequest.getName(), e);
            throw new RuntimeException("Ошибка при сохранении изображения", e);
        } catch (Exception e) {
            log.error("Ошибка при создании VIP продукта: {}", vipProductRequest.getName(), e);
            throw new RuntimeException("Ошибка при создании VIP продукта", e);
        }
    }

    public ResponseEntity<VipProductResponse> updateVipProduct(UUID id, VipProductRequest vipProductRequest) {
        // Валидация входных данных
        if (id == null) {
            throw new IllegalArgumentException("ID продукта не может быть null");
        }
        if (vipProductRequest == null) {
            throw new IllegalArgumentException("VipProductRequest не может быть null");
        }

        log.info("Обновление VIP продукта с ID: {}", id);

        try {
            Optional<VipProduct> optionalProduct = vipProductRepository.findById(id);
            if (optionalProduct.isEmpty()) {
                log.warn("VIP продукт с ID {} не найден", id);
                return ResponseEntity.notFound().build();
            }

            VipProduct vipProduct = optionalProduct.get();

            // Обновление названия, если оно предоставлено и не пусто
            if (vipProductRequest.getName() != null && !vipProductRequest.getName().trim().isEmpty()) {
                vipProduct.setName(vipProductRequest.getName().trim());
                log.debug("Обновлено название VIP продукта: {}", vipProductRequest.getName());
            }

            // Обновление изображения, если оно предоставлено
            if (vipProductRequest.getImage() != null && !vipProductRequest.getImage().isEmpty()) {
                FileEntity savedImage = fileUploadService.saveImage(vipProductRequest.getImage());
                vipProduct.setImage(savedImage);
                log.debug("Обновлено изображение VIP продукта с ID: {}", id);
            }

            vipProduct = vipProductRepository.save(vipProduct);
            log.info("VIP продукт с ID {} успешно обновлен", id);

            VipProductResponse updatedVipProduct = converterService.convertToVipProductResponse(vipProduct);
            return ResponseEntity.ok(updatedVipProduct);

        } catch (IOException e) {
            log.error("Ошибка при сохранении изображения для VIP продукта с ID: {}", id, e);
            throw new RuntimeException("Ошибка при сохранении изображения", e);
        } catch (Exception e) {
            log.error("Ошибка при обновлении VIP продукта с ID: {}", id, e);
            throw new RuntimeException("Ошибка при обновлении VIP продукта", e);
        }
    }

    public ResponseEntity<List<VipProductResponse>> getAllVipProducts() {
        log.info("Получение всех VIP продуктов");

        try {
            List<VipProduct> vipProducts = vipProductRepository.findAll();
            log.debug("Найдено {} VIP продуктов", vipProducts.size());

            List<VipProductResponse> vipProductResponses = new ArrayList<>();
            for (VipProduct vipProduct : vipProducts) {
                vipProductResponses.add(converterService.convertToVipProductResponse(vipProduct));
            }

            return ResponseEntity.ok(vipProductResponses);

        } catch (Exception e) {
            log.error("Ошибка при получении всех VIP продуктов", e);
            throw new RuntimeException("Ошибка при получении VIP продуктов", e);
        }
    }

    public void deleteVipProduct(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("ID продукта не может быть null");
        }

        log.info("Удаление VIP продукта с ID: {}", id);

        try {
            if (!vipProductRepository.existsById(id)) {
                log.warn("VIP продукт с ID {} не найден для удаления", id);
                throw new RuntimeException("VIP продукт с ID " + id + " не найден");
            }

            vipProductRepository.deleteById(id);
            log.info("VIP продукт с ID {} успешно удален", id);

        } catch (Exception e) {
            log.error("Ошибка при удалении VIP продукта с ID: {}", id, e);
            throw new RuntimeException("Ошибка при удалении VIP продукта", e);
        }
    }

    public VipProductResponse getVipProductById(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("ID продукта не может быть null");
        }

        log.info("Вывод VIP продукта по ID: {}", id);

        VipProduct vipProduct = vipProductRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("VIP продукт с ID " + id + " не найден"));

        return converterService.convertToVipProductResponse(vipProduct);
    }


}