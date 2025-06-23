package com.example.marketplace_backend.Service.Impl;

import com.example.marketplace_backend.Model.Subcategory;
import com.example.marketplace_backend.Repositories.CategoryRepository;
import com.example.marketplace_backend.Repositories.SubcategoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class SubcategoryServiceImpl extends BaseServiceImpl<Subcategory, UUID> {
    private final SubcategoryRepository subCategoryRepository;
    private final CategoryRepository categoryRepository;

    public SubcategoryServiceImpl(SubcategoryRepository subCategoryRepository, CategoryRepository categoryRepository) {
        super(subCategoryRepository);
        this.subCategoryRepository = subCategoryRepository;
        this.categoryRepository = categoryRepository;
    }

    public Subcategory getById(UUID id) {
        return subCategoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Subcategory not found with id: " + id));
    }

    public Subcategory save(Subcategory subcategory) {
        return subCategoryRepository.save(subcategory);
    }


    public List<Subcategory> getAll() {
        return subCategoryRepository.findAll();
    }

    public void delete(Subcategory subcategory) {
        subCategoryRepository.delete(subcategory);
    }

    public Subcategory getDeletedById(UUID id) {
        return subCategoryRepository.findDeletedById(id)
                .orElseThrow(() -> new RuntimeException("Deleted subcategory not found with id: " + id));
    }

}
