package com.example.marketplace_backend.Model.ProductSpec.LaptopSpec;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "ssds")
public class Ssd {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chip_id")
    private Chip chip;

    @OneToMany(mappedBy = "ssd", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Ram> rams;
}