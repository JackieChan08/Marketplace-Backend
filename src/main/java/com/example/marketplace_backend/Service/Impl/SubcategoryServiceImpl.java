package com.example.marketplace_backend.Service.Impl;

import com.example.marketplace_backend.Model.Subcategory;
import com.example.marketplace_backend.Repositories.SubcategoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;

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

}
