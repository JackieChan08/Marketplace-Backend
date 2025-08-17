package com.example.marketplace_backend.controller.admin;

import com.example.marketplace_backend.DTO.Requests.models.PhoneConnectionRequest;
import com.example.marketplace_backend.DTO.Responses.models.PhoneConnectionResponse;
import com.example.marketplace_backend.Service.Impl.PhoneConnectionServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/admin/connections")
@RequiredArgsConstructor
public class AdminPhoneConnectionController {
    private final PhoneConnectionServiceImpl phoneConnectionService;

    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<PhoneConnectionResponse> createConnection(
            @ModelAttribute PhoneConnectionRequest request
    ) {
        return ResponseEntity.ok(phoneConnectionService.create(request));
    }

    @GetMapping
    public ResponseEntity<List<PhoneConnectionResponse>> getAllConnections() {
        return ResponseEntity.ok(phoneConnectionService.getAll());
    }

    @PostMapping(value = "/update/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<PhoneConnectionResponse> updateConnection(
            @PathVariable UUID id,
            @ModelAttribute PhoneConnectionRequest request
    ) {
        return ResponseEntity.ok(phoneConnectionService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteConnection(
            @PathVariable UUID id)
    {
        phoneConnectionService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
