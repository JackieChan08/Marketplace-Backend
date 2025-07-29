package com.example.marketplace_backend.controller;

import com.example.marketplace_backend.Model.ProductSubParameters;
import com.example.marketplace_backend.Service.Impl.ProductSubParametersServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/product-sub-parameters")
public class ProductSubParametersController {
    private final ProductSubParametersServiceImpl productSubParametersService;


    @GetMapping("")
    public ResponseEntity<List<ProductSubParameters>> getAllProductSubParameters() {
        return ResponseEntity.ok(productSubParametersService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductSubParameters> getProductSubParameterById(@PathVariable UUID id) {
        Optional<ProductSubParameters> subParameter = productSubParametersService.findById(id);
        return subParameter.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/parameter/{parameterId}")
    public ResponseEntity<List<ProductSubParameters>> getProductSubParametersByParameterId(@PathVariable UUID parameterId) {
        if (!productSubParametersService.productParameterExists(parameterId)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(productSubParametersService.findByProductParameterId(parameterId));
    }


    @GetMapping("/search")
    public ResponseEntity<ProductSubParameters> findProductSubParameterByNameAndParameterId(
            @RequestParam String name,
            @RequestParam UUID parameterId) {

        if (!productSubParametersService.productParameterExists(parameterId)) {
            return ResponseEntity.notFound().build();
        }

        Optional<ProductSubParameters> subParameter = productSubParametersService.findByNameAndProductParameterId(name, parameterId);
        return subParameter.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
