package com.example.marketplace_backend.Service.Impl;

import com.example.marketplace_backend.Model.Brand;
import com.example.marketplace_backend.Model.Intermediate_objects.BrandImage;
import com.example.marketplace_backend.Model.Intermediate_objects.CategoryImage;
import com.example.marketplace_backend.Repositories.BrandImageRepository;
import com.example.marketplace_backend.Repositories.BrandRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class BrandServiceImpl extends BaseServiceImpl<Brand, UUID>{
    private final BrandRepository repository;
    private final BrandImageRepository brandImageRepository;
    private final FileUploadService fileUploadService;

    protected BrandServiceImpl(BrandRepository repository, BrandImageRepository brandImageRepository, FileUploadService fileUploadService) {
        super(repository);
        this.repository = repository;
        this.brandImageRepository = brandImageRepository;
        this.fileUploadService = fileUploadService;
    }

    public List<Brand> findAllActive() {
        return repository.findAllActive();
    }

    public Optional<Brand> findById(UUID brandId) {
        return repository.findById(brandId);
    }
    @Override
    public void delete(UUID id) {
        List<BrandImage>  brandImages = brandImageRepository.findByBrandId(id);
        for (BrandImage brandImage : brandImages) {
            fileUploadService.deleteImage(brandImage.getImage().getUniqueName());
        }
        brandImageRepository.deleteById(id);
    }
}
