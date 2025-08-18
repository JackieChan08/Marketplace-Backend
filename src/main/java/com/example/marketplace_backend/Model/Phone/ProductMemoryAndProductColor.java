package com.example.marketplace_backend.Model.Phone;

import com.example.marketplace_backend.Model.ProductColor;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "product_memory_and_product_color")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductMemoryAndProductColor {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "color_id", nullable = false)
    private ProductColor productColor;

    @ManyToOne
    @JoinColumn(name = "memory_id", nullable = false)
    private ProductMemory productMemory;

    @Column(nullable = false)
    private BigDecimal price;

}
