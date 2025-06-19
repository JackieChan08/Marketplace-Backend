package com.example.marketplace_backend.Service.Impl;

import com.example.marketplace_backend.Model.Brand;
import com.example.marketplace_backend.Repositories.BrandRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class BrandServiceImpl extends BaseServiceImpl<Brand, UUID>{
    private final BrandRepository repository;

    protected BrandServiceImpl(BrandRepository repository) {
        super(repository);
        this.repository = repository;
    }

    public List<Brand> findAllActive() {
        return repository.findAllActive();
    }

    public Optional<Brand> findById(UUID brandId) {
        return repository.findById(brandId);
    }
}
