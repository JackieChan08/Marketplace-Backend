package com.example.marketplace_backend.Model.ProductSpec.WatchSpec;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "dials")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Dial {
    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false, length = 30)
    private String name;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;
}