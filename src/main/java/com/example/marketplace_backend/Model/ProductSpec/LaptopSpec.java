package com.example.marketplace_backend.Model.ProductSpec;

import com.example.marketplace_backend.Model.ProductColor;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
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

    @Column(name = "ssd_memory", nullable = false)
    private String ssdMemory;

    @Column(nullable = false)
    private BigDecimal price;
}
