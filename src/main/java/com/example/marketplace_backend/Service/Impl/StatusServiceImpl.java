package com.example.marketplace_backend.Service.Impl;

import com.example.marketplace_backend.DTO.Requests.models.StatusRequest;
import com.example.marketplace_backend.Model.Category;
import com.example.marketplace_backend.Model.Order;
import com.example.marketplace_backend.Model.Product;
import com.example.marketplace_backend.Model.Statuses;
import com.example.marketplace_backend.Repositories.StatusRepository;
import lombok.RequiredArgsConstructor; // Добавил аннотацию
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor // Добавил аннотацию Lombok
public class StatusServiceImpl {

    private final StatusRepository statusRepository;
    private final ProductServiceImpl productServiceImpl;
    private final OrderServiceImpl orderServiceImpl;

    public List<Statuses> getAllStatuses() {
        return statusRepository.findAll();
    }

    public ResponseEntity<Statuses> createStatuses(StatusRequest request) {
        if (request == null || request.getName() == null || request.getName().trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        Statuses statuses = new Statuses();
        statuses.setName(request.getName());
        statuses.setColor(request.getColor());
        statuses.setOrderFlag(request.isOrderFlag());
        statuses.setProductFlag(request.isProductFlag());

        statusRepository.save(statuses);

        return ResponseEntity.ok(statuses);
    }

    public void deleteStatuses(UUID id) {
        if (id == null || !statusRepository.existsById(id)) {
            throw new IllegalArgumentException("Status not found with id: " + id);
        }
        statusRepository.deleteById(id);
    }

    public List<Statuses> getAllStatusesByOrder() {
        return statusRepository.findAllByOrderFlag();
    }

    public List<Statuses> getAllStatusesByProduct() {
        return statusRepository.findAllByProductFlag();
    }
}