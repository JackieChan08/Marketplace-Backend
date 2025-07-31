package com.example.marketplace_backend.controller;

import com.example.marketplace_backend.DTO.Responses.models.UserResponse;
import com.example.marketplace_backend.Model.User;
import com.example.marketplace_backend.Service.Impl.ConverterService;
import com.example.marketplace_backend.Service.Impl.auth.UserServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserController {
    private final UserServiceImpl userService;
    private final ConverterService converterService;

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getMe() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.getCurrentUser(email);
        UserResponse response = converterService.convertToUserResponse(user);
        return ResponseEntity.ok(response);
    }
}
