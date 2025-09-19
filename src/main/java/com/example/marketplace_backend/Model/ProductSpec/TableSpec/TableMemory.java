package com.example.marketplace_backend.Model.ProductSpec.TableSpec;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "table_memories")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TableMemory {
    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false, length = 30)
    private String name;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;
}
