package com.example.marketplace_backend.controller.admin;

import com.example.marketplace_backend.Model.ProductParameters;
import com.example.marketplace_backend.Service.Impl.ProductParametersServiceImpl;
import com.example.marketplace_backend.Service.Impl.ProductServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/product-parameters")
public class AdminProductParametersController {
    private final ProductServiceImpl  productService;
    private final ProductParametersServiceImpl  productParametersService;


    @GetMapping("/stats/product/{productId}")
    public ResponseEntity<Map<String, Long>> getProductParametersStats(@PathVariable UUID productId) {
        if (!productParametersService.productExists(productId)) {
            return ResponseEntity.notFound().build();
        }
        Map<String, Long> stats = new HashMap<>();
        stats.put("parametersCount", productParametersService.countByProductId(productId));
        return ResponseEntity.ok(stats);
    }


}
