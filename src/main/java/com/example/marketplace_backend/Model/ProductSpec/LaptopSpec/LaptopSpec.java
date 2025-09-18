package com.example.marketplace_backend.Model.ProductSpec.LaptopSpec;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "laptop_specs")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LaptopSpec {
    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "chip_id", nullable = false)
    private Chip chip;

    @ManyToOne(optional = false)
    @JoinColumn(name = "ssd_id", nullable = false)
    private Ssd ssd;

    @ManyToOne(optional = false)
    @JoinColumn(name = "ram_id", nullable = false)
    private Ram ram;
}
