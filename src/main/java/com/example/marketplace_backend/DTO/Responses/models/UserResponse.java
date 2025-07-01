package com.example.marketplace_backend.DTO.Responses.models;


import com.example.marketplace_backend.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class UserResponse {
    private UUID id;
    private String email;
    private String name;
    private Role role;
}

