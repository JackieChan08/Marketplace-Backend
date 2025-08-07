package com.example.marketplace_backend.controller;

import com.example.marketplace_backend.Model.Statuses;
import com.example.marketplace_backend.Service.Impl.StatusServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/statuses")
public class StatusController {
    private final StatusServiceImpl statusService;

    @GetMapping("/by-product")
    public ResponseEntity<List<Statuses>> getAllStatusesByProduct() {
        return ResponseEntity.ok(statusService.getAllStatusesByProduct());
    }
}
