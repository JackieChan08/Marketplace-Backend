package com.example.marketplace_backend.controller;

import com.example.marketplace_backend.Model.ProductParameters;
import com.example.marketplace_backend.Service.Impl.ProductParametersServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/product-parameters")
public class ProductParametersController {
    private final ProductParametersServiceImpl productParametersService;


    @GetMapping
    public ResponseEntity<List<ProductParameters>> getAllProductParameters() {
        return ResponseEntity.ok(productParametersService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductParameters> getProductParameterById(@PathVariable UUID id) {
        Optional<ProductParameters> parameter = productParametersService.findById(id);
        return parameter.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<List<ProductParameters>> getProductParametersByProductId(@PathVariable UUID productId) {
        if (!productParametersService.productExists(productId)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(productParametersService.findByProductId(productId));
    }


    @GetMapping("/search")
    public ResponseEntity<ProductParameters> findProductParameterByNameAndProductId(
            @RequestParam String name,
            @RequestParam UUID productId) {

        if (!productParametersService.productExists(productId)) {
            return ResponseEntity.notFound().build();
        }

        Optional<ProductParameters> parameter = productParametersService.findByNameAndProductId(name, productId);
        return parameter.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

}
