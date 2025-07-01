package com.example.marketplace_backend.Service.Impl;

import com.example.marketplace_backend.DTO.Requests.models.BrandRequest;
import com.example.marketplace_backend.Model.Brand;
import com.example.marketplace_backend.Model.FileEntity;
import com.example.marketplace_backend.Model.Intermediate_objects.BrandImage;
import com.example.marketplace_backend.Repositories.BrandImageRepository;
import com.example.marketplace_backend.Repositories.BrandRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class BrandServiceImpl extends BaseServiceImpl<Brand, UUID>{
    private final BrandRepository brandRepository;
    private final BrandImageRepository brandImageRepository;
    private final FileUploadService fileUploadService;

    @Autowired
    public BrandServiceImpl(BrandRepository brandRepository, BrandImageRepository brandImageRepository, FileUploadService fileUploadService) {
        super(brandRepository);
        this.brandRepository = brandRepository;
        this.brandImageRepository = brandImageRepository;
        this.fileUploadService = fileUploadService;
    }

    @Transactional(readOnly = true)
    public List<Brand> findAllActive() {
        return brandRepository.findAllActive();
    }

    @Transactional(readOnly = true)
    public List<Brand> findAllDeActive() {
        return brandRepository.findAllDeActive();
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

    @Override
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
    public void purgeOldBrands(LocalDateTime expirationDate) {
        brandRepository.purgeOldBrands(expirationDate);
    }

    @Override
    public void delete(UUID id) {
        try {
            List<BrandImage> brandImages = brandImageRepository.findByBrandId(id);

            for (BrandImage brandImage : brandImages) {
                if (brandImage.getImage() != null && brandImage.getImage().getUniqueName() != null) {
                    fileUploadService.deleteImage(brandImage.getImage().getUniqueName());
                }
            }

            brandRepository.deleteById(id);

        } catch (Exception e) {
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

    public Brand createBrand(BrandRequest request) throws IOException {

        Brand brand = new Brand();
        brand.setName(request.getName());
        brand.setDeletedAt(null);
        brand.setCreatedAt(LocalDateTime.now());

        brand = save(brand);

        List<BrandImage> brandImages = new ArrayList<>();
        for (MultipartFile image : request.getImages()) {
            FileEntity savedImage = fileUploadService.saveImage(image);
            BrandImage brandImage = BrandImage.builder()
                    .brand(brand)
                    .image(savedImage)
                    .build();
            brandImages.add(brandImage);
        }

        brand.setBrandImages(brandImages);

        brand = save(brand);
        return brand;
    }

    public Brand editBrand(UUID id, BrandRequest request) throws IOException {
        Brand brand = getById(id);

        if (request.getName() != null && !request.getName().isEmpty()) {
            brand.setName(request.getName());
        }

        if (request.getImages() != null && !request.getImages().isEmpty()) {
            List<BrandImage> newBrandImages = request.getImages().stream()
                    .map(image -> {
                        try {
                            FileEntity savedImage = fileUploadService.saveImage(image);
                            return BrandImage.builder()
                                    .brand(brand)
                                    .image(savedImage)
                                    .build();
                        } catch (IOException e) {
                            throw new RuntimeException("Ошибка при сохранении изображения", e);
                        }
                    })
                    .toList();

            if (brand.getBrandImages() != null) {
                brand.getBrandImages().addAll(newBrandImages);
            } else {
                brand.setBrandImages(newBrandImages);
            }
        }

        save(brand);
        return brand;
    }
}
