package com.example.marketplace_backend.Service.Impl;

import com.example.marketplace_backend.DTO.Requests.models.StatusRequest;
import com.example.marketplace_backend.DTO.Responses.models.StatusResponse;
import com.example.marketplace_backend.Model.Statuses;
import com.example.marketplace_backend.Repositories.StatusRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class StatusServiceImpl {
    private final StatusRepository statusRepository;
    private final ConverterService converterService;

    public List<Statuses> getAllStatuses() {
        return statusRepository.findAll();
    }

    public ResponseEntity<Statuses> createStatuses(StatusRequest request) {
        if (request == null || request.getName() == null || request.getName().trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        Statuses statuses = new Statuses();
        statuses.setName(request.getName());
        statuses.setPrimaryColor(request.getPrimaryColor());
        statuses.setBackgroundColor(request.getBackgroundColor());
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

    public List<StatusResponse> getAllStatusesByProduct() {
        return statusRepository.findAllByProductFlag().stream()
                .map(converterService::convertToStatusResponse)
                .toList();
    }

    public Statuses getStatusById(UUID id) {
        return statusRepository.findById(id).orElse(null);
    }
}