package com.example.marketplace_backend.controller.admin;

import com.example.marketplace_backend.DTO.Responses.models.ProductParameterResponse;
import com.example.marketplace_backend.Model.ProductParameters;
import com.example.marketplace_backend.Service.Impl.ConverterService;
import com.example.marketplace_backend.Service.Impl.ProductParametersServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.stream.Collectors;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/product-parameters")
public class AdminProductParametersController {
    private final ProductParametersServiceImpl  productParametersService;
    private final ConverterService converterService;


    @GetMapping("/stats/product/{productId}")
    public ResponseEntity<Map<String, Long>> getProductParametersStats(@PathVariable UUID productId) {
        if (!productParametersService.productExists(productId)) {
            return ResponseEntity.notFound().build();
        }
        Map<String, Long> stats = new HashMap<>();
        stats.put("parametersCount", productParametersService.countByProductId(productId));
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/template")
    public ResponseEntity<List<ProductParameterResponse>> getProductParametersTemplate() {
        List<ProductParameters> parameters = productParametersService.findAll();

        // Убираем дубликаты по name и сортируем по name
        List<ProductParameterResponse> response = parameters.stream()
                .sorted(Comparator.comparing(ProductParameters::getName))
                .collect(Collectors.toMap(
                        ProductParameters::getName,
                        converterService::convertToProductParameterResponse,
                        (existing, replacement) -> existing, // если дубликат — берём первый
                        LinkedHashMap::new
                ))
                .values()
                .stream()
                .toList();

        return ResponseEntity.ok(response);
    }


}
