package com.example.marketplace_backend.Model.ProductSpec.WatchSpec;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "watch_spec")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WatchSpec {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column
    private String strapSize;

    @Column
    private BigDecimal sizeMm;

    @Column
    private BigDecimal price;
}
