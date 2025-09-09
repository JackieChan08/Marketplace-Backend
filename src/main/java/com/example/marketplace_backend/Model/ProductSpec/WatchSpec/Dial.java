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
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column
    private String size_mm;

    @Column
    private BigDecimal price;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "strap_size_id")
    private StrapSize strapSize;
}
