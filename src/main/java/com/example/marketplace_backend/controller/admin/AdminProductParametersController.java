package com.example.marketplace_backend.controller.admin;

import com.example.marketplace_backend.Service.Impl.ProductParametersServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/product-parameters")
public class AdminProductParametersController {
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
