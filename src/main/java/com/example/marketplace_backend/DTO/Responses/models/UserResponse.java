package com.example.marketplace_backend.DTO.Responses.models;

import com.example.marketplace_backend.enums.Role;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
public class UserResponse {
    private UUID id;
    private String email;
    private String name;
    private String phoneNumber;
    private String address;
    private Role role;
    private LocalDateTime createdAt;
    private List<FileResponse> images;
}
