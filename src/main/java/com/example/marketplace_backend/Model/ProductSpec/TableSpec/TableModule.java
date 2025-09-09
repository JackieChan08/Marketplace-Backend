package com.example.marketplace_backend.Model.ProductSpec.TableSpec;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "table_modules")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TableModule {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "table_spec_id")
    private TableSpec tableSpec;

    @OneToMany(mappedBy = "tableModule", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TableMemory> memories;
}
