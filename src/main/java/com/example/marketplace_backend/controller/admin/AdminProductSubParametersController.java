package com.example.marketplace_backend.controller.admin;

import com.example.marketplace_backend.Model.ProductSubParameters;
import com.example.marketplace_backend.Service.Impl.ProductSubParametersServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/product-sub-parameters")
public class AdminProductSubParametersController {
    private final ProductSubParametersServiceImpl productSubParametersService;


    @GetMapping("/stats/parameter/{parameterId}")
    public ResponseEntity<Map<String, Long>> getProductSubParametersStats(@PathVariable UUID parameterId) {
        if (!productSubParametersService.productParameterExists(parameterId)) {
            return ResponseEntity.notFound().build();
        }
        Map<String, Long> stats = new HashMap<>();
        List<ProductSubParameters> subParams = productSubParametersService.findByProductParameterId(parameterId);
        stats.put("subParametersCount", (long) subParams.size());
        return ResponseEntity.ok(stats);
    }


    @PostMapping("/create")
    public ResponseEntity<ProductSubParameters> createProductSubParameter(@RequestBody ProductSubParameters productSubParameters) {
        try {
            ProductSubParameters created = productSubParametersService.create(productSubParameters);
            return ResponseEntity.ok(created);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/create/batch")
    public ResponseEntity<List<ProductSubParameters>> createProductSubParametersBatch(@RequestBody List<ProductSubParameters> productSubParameters) {
        try {
            List<ProductSubParameters> created = productSubParametersService.createBatch(productSubParameters);
            return ResponseEntity.ok(created);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/edit/{id}")
    public ResponseEntity<ProductSubParameters> editProductSubParameter(
            @PathVariable UUID id,
            @RequestBody ProductSubParameters updatedSubParameters) {
        try {
            ProductSubParameters updated = productSubParametersService.update(id, updatedSubParameters);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/edit/{id}/value")
    public ResponseEntity<ProductSubParameters> editProductSubParameterValue(
            @PathVariable UUID id,
            @RequestParam String value) {
        try {
            ProductSubParameters updated = productSubParametersService.updateValue(id, value);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProductSubParameter(@PathVariable UUID id) {
        try {
            productSubParametersService.deleteById(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/parameter/{parameterId}")
    public ResponseEntity<Void> deleteAllProductSubParametersByParameterId(@PathVariable UUID parameterId) {
        try {
            if (!productSubParametersService.productParameterExists(parameterId)) {
                return ResponseEntity.notFound().build();
            }
            productSubParametersService.deleteByProductParameterId(parameterId);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
