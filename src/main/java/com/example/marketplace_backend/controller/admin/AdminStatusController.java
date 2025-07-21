package com.example.marketplace_backend.controller.admin;

import com.example.marketplace_backend.DTO.Requests.models.StatusRequest;
import com.example.marketplace_backend.Model.Statuses;
import com.example.marketplace_backend.Service.Impl.StatusServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/admin/statuses")
public class AdminStatusController {
    private final StatusServiceImpl statusService;

    @GetMapping
    public ResponseEntity<List<Statuses>> getAllStatuses() {
        return ResponseEntity.ok(statusService.getAllStatuses());
    }

    @PostMapping(value = "/create", consumes = {"multipart/form-data"})
    public ResponseEntity<Statuses> createStatuses(
            @ModelAttribute StatusRequest request
    ) {
        try {
            return statusService.createStatuses(request);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteStatuses(@PathVariable UUID id) {
        try {
            statusService.deleteStatuses(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}