package com.example.marketplace_backend.Model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import java.util.UUID;

@Entity
public class DummyEntity {
    @Id
    private UUID id;
}

