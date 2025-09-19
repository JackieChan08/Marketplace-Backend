package com.example.marketplace_backend.Model.ProductSpec.TableSpec;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "table_spec")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TableSpec {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "table_memory_id")
    private TableMemory tableMemory;

    @ManyToOne(optional = false)
    @JoinColumn(name = "table_module_id")
    private TableModule tableModule;
}
