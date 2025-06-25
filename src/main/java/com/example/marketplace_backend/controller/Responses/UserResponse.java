package com.example.marketplace_backend.controller.Responses;


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

