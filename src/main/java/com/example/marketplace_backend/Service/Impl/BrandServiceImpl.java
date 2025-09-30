package com.example.marketplace_backend.Service.Impl;

import com.example.marketplace_backend.DTO.Requests.models.BrandRequest;
import com.example.marketplace_backend.DTO.Responses.models.BrandResponse;
import com.example.marketplace_backend.Model.Brand;
import com.example.marketplace_backend.Model.FileEntity;
import com.example.marketplace_backend.Model.Intermediate_objects.BrandImage;
import com.example.marketplace_backend.Model.Intermediate_objects.CategoryImage;
import com.example.marketplace_backend.Repositories.BrandImageRepository;
import com.example.marketplace_backend.Repositories.BrandRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
public class BrandServiceImpl{
    private final BrandRepository brandRepository;
    private final BrandImageRepository brandImageRepository;
    private final FileUploadService fileUploadService;
    private final ConverterService converterService;

    @Autowired
    public BrandServiceImpl(BrandRepository brandRepository, BrandImageRepository brandImageRepository, FileUploadService fileUploadService, ConverterService converterService) {
        this.brandRepository = brandRepository;
        this.brandImageRepository = brandImageRepository;
        this.fileUploadService = fileUploadService;
        this.converterService = converterService;
    }

    @Transactional(readOnly = true)
    public List<Brand> findAllActive() {
        return brandRepository.findAllActive();
    }

    @Transactional(readOnly = true)
    public List<Brand> findAllDeActive() {
        return brandRepository.findAllDeActive();
    }

    public Page<Brand> findAllDeActive(Pageable pageable) {
        return brandRepository.findAllDeActive(pageable);
    }
    public Page<Brand> findAllActive(Pageable pageable) {
        return brandRepository.findAllActive(pageable);
    }

    @Transactional(readOnly = true)
    public List<Brand> findAllWithProducts() {
        return brandRepository.findAllWithProducts();
    }

    @Transactional(readOnly = true)
    public Page<Brand> findAll(Pageable pageable) {
        return brandRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Optional<Brand> findById(UUID brandId) {
        return brandRepository.findById(brandId);
    }

    public Brand save(Brand brand) {
        if (brand.getId() == null) {
            brand.setCreatedAt(LocalDateTime.now());
        } else {
            brand.setUpdatedAt(LocalDateTime.now());
        }
        return brandRepository.save(brand);
    }

    @Transactional
    public void softDelete(UUID id) {
        brandRepository.softDeleteById(id, LocalDateTime.now());
    }

    @Transactional
    public void restore(UUID id) {
        Optional<Brand> brandOpt = brandRepository.findById(id);
        if (brandOpt.isPresent()) {
            Brand brand = brandOpt.get();
            brand.setDeletedAt(null);
            brand.setUpdatedAt(LocalDateTime.now());
            brandRepository.save(brand);
        }
    }

    @Transactional
    public void purgeOldBrands() {
        List<Brand> brandsToDelete = brandRepository.findAllDeActive();
        brandRepository.deleteAll(brandsToDelete);
    }

    public void delete(UUID id) {
        try {
            brandRepository.deleteById(id);
        } catch (Exception e) {
            log.error("Ошибка при удалении бренда с ID: {}", id, e);  // <-- обязательно с `e`
            throw new RuntimeException("Ошибка при удалении бренда с ID: " + id, e);
        }

    }

    @Transactional(readOnly = true)
    public long countActiveBrands() {
        return findAllActive().size();
    }

    @Transactional(readOnly = true)
    public long countDeActiveBrands() {
        return findAllDeActive().size();
    }

    private void deleteOldBrandImage(Brand brand) {
        if (brand.getBrandImages() != null && !brand.getBrandImages().isEmpty()) {
            BrandImage oldImage = brand.getBrandImages().get(0);
            if (oldImage.getImage() != null && oldImage.getImage().getUniqueName() != null) {
                try {
                    fileUploadService.deleteImage(oldImage.getImage().getUniqueName());
                } catch (Exception e) {
                    System.err.println("Ошибка при удалении старого изображения: " + e.getMessage());
                }
            }
        }
    }

    public Brand createBrand(BrandRequest request) throws IOException {
        Brand brand = new Brand();
        brand.setName(request.getName());
        brand.setDeletedAt(null);
        brand.setCreatedAt(LocalDateTime.now());

        brand = save(brand); // сохраняем бренд

        // Сохраняем изображение
        FileEntity savedImage = fileUploadService.saveImage(request.getImage());

        // Привязываем изображение
        BrandImage brandImage = BrandImage.builder()
                .brand(brand)
                .image(savedImage)
                .build();

        brandImageRepository.save(brandImage);

        List<BrandImage> images = new ArrayList<>();
        images.add(brandImage);
        brand.setBrandImages(images);


        brand = save(brand);
        return brand;
    }


    @Transactional
    public Brand editBrand(UUID id, BrandRequest request) throws IOException {
        Brand brand = findById(id)
                .orElseThrow(() -> new RuntimeException("Brand not found"));

        if (request.getName() != null && !request.getName().isEmpty()) {
            brand.setName(request.getName());
        }

        if (request.getImage() != null && !request.getImage().isEmpty()) {
            FileEntity savedImage = fileUploadService.saveImage(request.getImage());

            BrandImage brandImage = BrandImage.builder()
                    .brand(brand)
                    .image(savedImage)
                    .build();

            if (brand.getBrandImages() == null) {
                brand.setBrandImages(new ArrayList<>());
            } else {
                brand.getBrandImages().clear();
            }
            brand.getBrandImages().add(brandImage);
        }

        brand.setUpdatedAt(LocalDateTime.now());
        return save(brand);
    }

    @Transactional
    public void deleteBrandImage(UUID brandId) {
        Brand brand = findById(brandId)
                .orElseThrow(() -> new RuntimeException("Brand not found"));;

        if (brand.getBrandImages() != null && !brand.getBrandImages().isEmpty()) {
            // Удаляем изображение из файловой системы
            deleteOldBrandImage(brand);

            // Очищаем коллекцию изображений
            brand.setBrandImages(new ArrayList<>());
            save(brand);
        }
    }

    public List<BrandResponse> getAllBrands() {
        return brandRepository.findAllActive().stream()
                .map(converterService::convertToBrandResponse)
                .toList();
    }


}