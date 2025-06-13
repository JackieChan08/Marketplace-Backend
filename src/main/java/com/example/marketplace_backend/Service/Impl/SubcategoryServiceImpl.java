package com.example.marketplace_backend.Service.Impl;

import com.example.marketplace_backend.Model.Subcategory;
import com.example.marketplace_backend.Repositories.SubcategoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SubcategoryServiceImpl extends BaseServiceImpl<Subcategory, Long>{
    private final SubcategoryRepository repository;

    protected SubcategoryServiceImpl(SubcategoryRepository repository) {
        super(repository);
        this.repository = repository;
    }

    public List<Subcategory> findAllActive() {
        return repository.findAllActive();
    }

    public Optional<Subcategory> findById(Long subcategoryId) {
        return repository.findById(subcategoryId);
    }
}
