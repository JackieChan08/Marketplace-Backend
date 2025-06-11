package com.example.marketplace_backend.Model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.Instant;

@Entity
@Data
@Table(name = "refresh_token")
public class RefreshToken {
    @Id @GeneratedValue
    private Long id;

    @Column(unique = true)
    private String token;

    @OneToOne
    private User user;

    private Instant expiryDate;
}