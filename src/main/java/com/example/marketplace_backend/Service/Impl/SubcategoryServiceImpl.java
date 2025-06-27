package com.example.marketplace_backend.Service.Impl;

import com.example.marketplace_backend.Model.Category;
import com.example.marketplace_backend.Model.Intermediate_objects.SubcategoryImage;
import com.example.marketplace_backend.Model.Subcategory;
import com.example.marketplace_backend.Repositories.CategoryRepository;
import com.example.marketplace_backend.Repositories.SubcategoryImageRepository;
import com.example.marketplace_backend.Repositories.SubcategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class SubcategoryServiceImpl extends BaseServiceImpl<Subcategory, UUID> {
    private final SubcategoryRepository subcategoryRepository;
    private final SubcategoryImageRepository subcategoryImageRepository;
    private final FileUploadService fileUploadService;

    @Autowired
    public SubcategoryServiceImpl(SubcategoryRepository subcategoryRepository,
                                  SubcategoryImageRepository subcategoryImageRepository,
                                  FileUploadService fileUploadService) {
        super(subcategoryRepository);
        this.subcategoryRepository = subcategoryRepository;
        this.subcategoryImageRepository = subcategoryImageRepository;
        this.fileUploadService = fileUploadService;
    }

    @Transactional(readOnly = true)
    public List<Subcategory> findAllActive() {
        return subcategoryRepository.findAll().stream()
                .filter(s -> s.getDeletedAt() == null)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<Subcategory> findAllDeActive() {
        return subcategoryRepository.findAll().stream()
                .filter(s -> s.getDeletedAt() != null)
                .toList();
    }

    @Transactional(readOnly = true)
    public Optional<Subcategory> findActiveById(UUID id) {
        return subcategoryRepository.findAllActive(id);
    }

    @Transactional(readOnly = true)
    public Optional<Subcategory> findDeActiveById(UUID id) {
        return subcategoryRepository.findAllDeActive(id);
    }

    @Transactional(readOnly = true)
    public List<Subcategory> findByCategoryActive(Category category) {
        return subcategoryRepository.findByCategoryActive(category);
    }

    @Transactional(readOnly = true)
    public List<Subcategory> findByCategoryDeActive(Category category) {
        return subcategoryRepository.findByCategoryDeActive(category);
    }

    @Transactional(readOnly = true)
    public Page<Subcategory> findAll(Pageable pageable) {
        return subcategoryRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Optional<Subcategory> findById(UUID subcategoryId) {
        return subcategoryRepository.findById(subcategoryId);
    }

    @Override
    public Subcategory save(Subcategory subcategory) {
        if (subcategory.getId() == null) {
            subcategory.setCreatedAt(LocalDateTime.now());
        } else {
            subcategory.setUpdatedAt(LocalDateTime.now());
        }
        return subcategoryRepository.save(subcategory);
    }

    @Transactional
    public void softDelete(UUID id) {
        subcategoryRepository.softDeleteById(id, LocalDateTime.now());
    }

    @Transactional
    public void restore(UUID id) {
        Optional<Subcategory> subcategoryOpt = subcategoryRepository.findById(id);
        if (subcategoryOpt.isPresent()) {
            Subcategory subcategory = subcategoryOpt.get();
            subcategory.setDeletedAt(null);
            subcategory.setUpdatedAt(LocalDateTime.now());
            subcategoryRepository.save(subcategory);
        }
    }

    @Transactional
    public void purgeOldSubcategories(LocalDateTime expirationDate) {
        subcategoryRepository.purgeOldSubcategories(expirationDate);
    }

    @Override
    public void delete(UUID id) {
        try {
            List<SubcategoryImage> subcategoryImages = subcategoryImageRepository.findBySubcategoryId(id);

            for (SubcategoryImage subcategoryImage : subcategoryImages) {
                if (subcategoryImage.getImage() != null && subcategoryImage.getImage().getUniqueName() != null) {
                    fileUploadService.deleteImage(subcategoryImage.getImage().getUniqueName());
                }
            }

            subcategoryRepository.deleteById(id);

        } catch (Exception e) {
            throw new RuntimeException("Ошибка при удалении подкатегории с ID: " + id, e);
        }
    }

    @Transactional(readOnly = true)
    public long countActiveSubcategories() {
        return findAllActive().size();
    }

    @Transactional(readOnly = true)
    public long countDeActiveSubcategories() {
        return findAllDeActive().size();
    }

    @Transactional(readOnly = true)
    public long countActiveByCategoryId(UUID categoryId) {
        return subcategoryRepository.findAll().stream()
                .filter(s -> s.getDeletedAt() == null &&
                        s.getCategory() != null &&
                        s.getCategory().getId().equals(categoryId))
                .count();
    }

    @Transactional(readOnly = true)
    public long countDeActiveByCategoryId(UUID categoryId) {
        return subcategoryRepository.findAll().stream()
                .filter(s -> s.getDeletedAt() != null &&
                        s.getCategory() != null &&
                        s.getCategory().getId().equals(categoryId))
                .count();
    }
}
