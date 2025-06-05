package com.example.marketplace_backend.controller;


import com.example.marketplace_backend.Model.Subcategory;
import com.example.marketplace_backend.Service.Impl.SubcategoryServiceImpl;
import com.example.marketplace_backend.controller.Responses.FileResponse;
import com.example.marketplace_backend.controller.Responses.SubcategoryResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/subcategories")
@RequiredArgsConstructor
@Tag(name = "Subcategory Controller", description = "API для управления подкатегориями")
public class SubcategoryController {

    private final SubcategoryServiceImpl subcategoryService;

    @GetMapping("/{id}")
    public ResponseEntity<SubcategoryResponse> getById(@PathVariable Long id) {
        Subcategory subcategory = subcategoryService.getById(id);
        if (subcategory == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        SubcategoryResponse response = convertToResponse(subcategory);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/list")
    public ResponseEntity<List<SubcategoryResponse>> getAll() {
        List<Subcategory> subcategories = subcategoryService.findAllActive(); // Предполагается метод фильтрации по deleted
        if (subcategories.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        List<SubcategoryResponse> responses = subcategories.stream()
                .map(this::convertToResponse)
                .toList();

        return ResponseEntity.ok(responses);
    }

    private SubcategoryResponse convertToResponse(Subcategory subcategory) {
        SubcategoryResponse response = new SubcategoryResponse();
        response.setId(subcategory.getId());
        response.setName(subcategory.getName());
        response.setDescription(subcategory.getDescription());

        if (subcategory.getCategory() != null) {
            response.setCategoryId(subcategory.getCategory().getId());
            response.setCategoryName(subcategory.getCategory().getName());
        }

        if (subcategory.getSubcategory() != null) {
            response.setParentSubcategoryId(subcategory.getSubcategory().getId());
            response.setParentSubcategoryName(subcategory.getSubcategory().getName());
        }

        if (subcategory.getImage() != null) {
            FileResponse fileResponse = new FileResponse();
            fileResponse.setUniqueName(subcategory.getImage().getUniqueName());
            fileResponse.setOriginalName(subcategory.getImage().getOriginalName());
            fileResponse.setUrl("http://localhost:8080/uploads/" + subcategory.getImage().getUniqueName());
            fileResponse.setFileType(subcategory.getImage().getFileType());
            response.setImage(fileResponse);
        }

        return response;
    }
}

