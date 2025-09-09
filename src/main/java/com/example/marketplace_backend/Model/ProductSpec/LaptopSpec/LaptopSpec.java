package com.example.marketplace_backend.Model.ProductSpec.LaptopSpec;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "laptop_spec")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LaptopSpec {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "title", nullable = false)
    private String title;

    @OneToMany(mappedBy = "laptopSpec", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Chip> chips;
}
