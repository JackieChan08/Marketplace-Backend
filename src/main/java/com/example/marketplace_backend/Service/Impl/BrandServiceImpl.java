package com.example.marketplace_backend.Service.Impl;

import com.example.marketplace_backend.Model.Brand;
import com.example.marketplace_backend.Repositories.BrandImageRepository;
import com.example.marketplace_backend.Repositories.BrandRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class BrandServiceImpl extends BaseServiceImpl<Brand, UUID>{
    private final BrandRepository brandRepository;
    private final BrandImageRepository brandImageRepository;
    private final FileUploadService fileUploadService;

    protected BrandServiceImpl(BrandRepository repository, BrandImageRepository brandImageRepository, FileUploadService fileUploadService, BrandRepository brandRepository) {
        super(repository);
        this.brandRepository = repository;
        this.brandImageRepository = brandImageRepository;
        this.fileUploadService = fileUploadService;
    }

    public List<Brand> findAllActive() {
        return brandRepository.findAllActive();
    }

    public Optional<Brand> findById(UUID brandId) {
        return brandRepository.findById(brandId);
    }

    @Override
    public void delete(UUID id) {
        Brand brand = brandRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Brand not found"));
        brandRepository.delete(brand);
    }
}
