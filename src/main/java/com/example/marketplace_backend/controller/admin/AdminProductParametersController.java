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



    @GetMapping()
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

    @GetMapping("/stats/product/{productId}")
    public ResponseEntity<Map<String, Long>> getProductParametersStats(@PathVariable UUID productId) {
        if (!productParametersService.productExists(productId)) {
            return ResponseEntity.notFound().build();
        }
        Map<String, Long> stats = new HashMap<>();
        stats.put("parametersCount", productParametersService.countByProductId(productId));
        return ResponseEntity.ok(stats);
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

    @PostMapping("/create")
    public ResponseEntity<ProductParameters> createProductParameter(@RequestBody ProductParameters productParameters) {
        try {
            ProductParameters created = productParametersService.create(productParameters);
            return ResponseEntity.ok(created);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/edit/{id}")
    public ResponseEntity<ProductParameters> editProductParameter(
            @PathVariable UUID id,
            @RequestBody ProductParameters updatedParameters) {
        try {
            ProductParameters updated = productParametersService.update(id, updatedParameters);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProductParameter(@PathVariable UUID id) {
        try {
            productParametersService.deleteById(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/product/{productId}")
    public ResponseEntity<Void> deleteAllProductParametersByProductId(@PathVariable UUID productId) {
        try {
            if (!productParametersService.productExists(productId)) {
                return ResponseEntity.notFound().build();
            }
            productParametersService.deleteByProductId(productId);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}
