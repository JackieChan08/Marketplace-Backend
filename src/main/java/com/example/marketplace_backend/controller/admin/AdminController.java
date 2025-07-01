package com.example.marketplace_backend.controller.admin;

import com.example.marketplace_backend.Repositories.BrandRepository;
import com.example.marketplace_backend.Repositories.SubcategoryRepository;
import com.example.marketplace_backend.Service.Impl.*;
import com.example.marketplace_backend.DTO.Responses.models.UserResponse;
import com.example.marketplace_backend.Service.Impl.ProductParametersServiceImpl;
import com.example.marketplace_backend.Service.Impl.ProductSubParametersServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
public class AdminController {
    private final UserServiceImpl userService;
    private final OrderServiceImpl orderService;
    private final ProductServiceImpl productService;
    private final CategoryServiceImpl categoryService;
    private final BrandServiceImpl brandService;
    private final FileUploadService fileUploadService;
    private final BrandRepository brandRepository;
    private final SubcategoryRepository subcategoryRepository;
    private final SubcategoryServiceImpl subcategoryService;
    private final ProductParametersServiceImpl productParametersService;
    private final ProductSubParametersServiceImpl productSubParametersService;


    //User
    @GetMapping("/users/search")
    public ResponseEntity<Page<UserResponse>> searchUsers(
            @RequestParam String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(userService.searchUsers(query, pageable));
    }
}