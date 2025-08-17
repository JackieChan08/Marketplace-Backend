package com.example.marketplace_backend.Model.Phone;

import com.example.marketplace_backend.Model.ProductColor;
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

    @Column(nullable = false, unique = true)
    private String memory;
}
