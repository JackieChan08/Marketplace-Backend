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
@Table(name = "chips")
public class Chip {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "laptop_spec_id")
    private LaptopSpec laptopSpec;

    @OneToMany(mappedBy = "chip", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Ssd> ssds;
}
