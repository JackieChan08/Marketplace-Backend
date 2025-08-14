package com.example.marketplace_backend.Model;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "product_memories")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductMemory {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "color_id", nullable = false)
    private ProductColor color;

    @Column(nullable = false)
    private String memory;
}
