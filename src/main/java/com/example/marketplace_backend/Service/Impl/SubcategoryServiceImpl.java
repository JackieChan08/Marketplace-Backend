package com.example.marketplace_backend.Service.Impl;

import com.example.marketplace_backend.DTO.Requests.models.SubcategoryRequest;
import com.example.marketplace_backend.DTO.Responses.models.SubcategoryResponse;
import com.example.marketplace_backend.Model.Category;
import com.example.marketplace_backend.Model.FileEntity;
import com.example.marketplace_backend.Model.Intermediate_objects.CategoryImage;
import com.example.marketplace_backend.Model.Intermediate_objects.SubcategoryImage;
import com.example.marketplace_backend.Model.Subcategory;
import com.example.marketplace_backend.Repositories.CategoryRepository;
import com.example.marketplace_backend.Repositories.SubcategoryImageRepository;
import com.example.marketplace_backend.Repositories.SubcategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
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
public class SubcategoryServiceImpl extends BaseServiceImpl<Subcategory, UUID> {
    private final SubcategoryRepository subcategoryRepository;
    private final SubcategoryImageRepository subcategoryImageRepository;
    private final FileUploadService fileUploadService;
    private final CategoryServiceImpl categoryService;
    private final ConverterService converterService;

    @Autowired
    public SubcategoryServiceImpl(SubcategoryRepository subcategoryRepository,
                                  SubcategoryImageRepository subcategoryImageRepository,
                                  FileUploadService fileUploadService,
                                  CategoryServiceImpl categoryService,
                                  ConverterService converterService) {
        super(subcategoryRepository);
        this.subcategoryRepository = subcategoryRepository;
        this.subcategoryImageRepository = subcategoryImageRepository;
        this.fileUploadService = fileUploadService;
        this.categoryService = categoryService;
        this.converterService = converterService;
    }

    @Transactional(readOnly = true)
    public List<Subcategory> findAllActive() {
        return subcategoryRepository.findAllActive();
    }

    @Transactional(readOnly = true)
    public Page<Subcategory> findAllDeActive(Pageable pageable) {
        return subcategoryRepository.findAllDeActive(pageable);
    }

    @Transactional(readOnly = true)
    public Subcategory findActiveById(UUID id) {
        return subcategoryRepository.findByIdAndDeletedAtIsNull(id);
    }

    @Transactional(readOnly = true)
    public Subcategory findDeActiveById(UUID id) {
        return subcategoryRepository.findByIdAndDeletedAtIsNotNull(id);
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
    public void purgeOldSubcategories() {
        List<Subcategory> subcategoriesToDelete = subcategoryRepository.findAllDeActiveList();
        subcategoryRepository.deleteAll(subcategoriesToDelete);
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

            subcategoryImageRepository.deleteAll(subcategoryImages);

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
        return findAllDeActiveList().size();
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
    public ResponseEntity<SubcategoryResponse> createSubcategory(SubcategoryRequest request) throws IOException {
        Optional<Category> categoryOpt = categoryService.findById(request.getCategoryId());
        if (categoryOpt.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        Subcategory subcategory = new Subcategory();
        subcategory.setName(request.getName());
        subcategory.setCategory(categoryOpt.get());
        subcategory.setDeletedAt(null);
        subcategory.setCreatedAt(LocalDateTime.now());

        subcategory = save(subcategory);

        SubcategoryResponse subcategoryResponse = converterService.convertToSubcategoryResponse(subcategory);
        return ResponseEntity.ok(subcategoryResponse);
    }


    public ResponseEntity<Subcategory> updateSubcategory(UUID id, SubcategoryRequest request) throws IOException {
        Subcategory subcategory = getById(id);

        if (request.getName() != null && !request.getName().isEmpty()) {
            subcategory.setName(request.getName());
        }

        if (request.getCategoryId() != null) {
            Optional<Category> categoryOpt = categoryService.findById(request.getCategoryId());
            categoryOpt.ifPresent(subcategory::setCategory);
        }

        subcategory.setUpdatedAt(LocalDateTime.now());
        save(subcategory);
        return ResponseEntity.ok(subcategory);
    }

    public boolean deleteSubcategoryImage(UUID subcategoryId, UUID imageId) {
        Optional<Subcategory> subcategoryOpt = subcategoryRepository.findById(subcategoryId);
        if (subcategoryOpt.isEmpty()) {
            return false;
        }

        Subcategory subcategory = subcategoryOpt.get();

        boolean removed = false;
        if (subcategory.getSubcategoryImages() != null) {
            removed = subcategory.getSubcategoryImages().removeIf(img ->
                    img.getImage() != null && img.getImage().getId().equals(imageId)
            );
        }

        if (removed) {
            subcategoryRepository.save(subcategory);
        }

        return removed;
    }

    public Page<Subcategory> findAllActive(Pageable pageable){
        return subcategoryRepository.findAllByDeletedAtIsNull(pageable);
    }
    public Page<Subcategory> findByCategoryActive(Category category, Pageable pageable){
        return subcategoryRepository.findByCategoryAndDeletedAtIsNull(category, pageable);
    }
    public Page<Subcategory> searchByName(String query, Pageable pageable){
        return subcategoryRepository.findByNameContainingIgnoreCaseAndDeletedAtIsNull(query, pageable);
    }

    public List<Subcategory> findAllDeActiveList(){
        return subcategoryRepository.findAllDeActiveList();
    }

}
