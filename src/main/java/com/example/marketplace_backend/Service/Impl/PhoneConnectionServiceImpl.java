package com.example.marketplace_backend.Service.Impl;

import com.example.marketplace_backend.DTO.Requests.models.PhoneConnectionRequest;
import com.example.marketplace_backend.DTO.Responses.models.PhoneConnectionResponse;
import com.example.marketplace_backend.Model.Phone.PhoneConnection;
import com.example.marketplace_backend.Repositories.PhoneConnectionRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class PhoneConnectionServiceImpl {
    private final PhoneConnectionRepository phoneConnectionRepository;
    private final ConverterService converterService;

    public PhoneConnectionServiceImpl(PhoneConnectionRepository phoneConnectionRepository,
                                      ConverterService converterService) {
        this.phoneConnectionRepository = phoneConnectionRepository;
        this.converterService = converterService;
    }

    public PhoneConnectionResponse create(PhoneConnectionRequest request) {
        PhoneConnection phoneConnection = new PhoneConnection();
        phoneConnection.setId(request.getId());
        phoneConnection.setSimType(request.getSimType());
        phoneConnectionRepository.save(phoneConnection);

        return converterService.convertToPhoneConnectionResponse(phoneConnection);
    }

    public PhoneConnectionResponse update(UUID id, PhoneConnectionRequest request) {
        PhoneConnection phoneConnection = phoneConnectionRepository.findById(id).orElseThrow(EntityNotFoundException::new);

        phoneConnection.setSimType(request.getSimType());
        phoneConnectionRepository.save(phoneConnection);

        return converterService.convertToPhoneConnectionResponse(phoneConnection);
    }

    public void delete(UUID id) {
        if (!phoneConnectionRepository.existsById(id)) {
            throw new EntityNotFoundException("PhoneConnection not found with id: " + id);
        }
        phoneConnectionRepository.deleteById(id);
    }

    public List<PhoneConnectionResponse> getAll() {
        return phoneConnectionRepository.findAll().stream()
                .map(converterService::convertToPhoneConnectionResponse)
                .toList();
    }
}
