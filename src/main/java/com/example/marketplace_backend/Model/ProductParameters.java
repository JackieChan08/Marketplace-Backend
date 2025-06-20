package com.example.marketplace_backend.Model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "product_parameters")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductParameters {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @OneToMany(mappedBy = "productParameter", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ProductSubParameters> productSubParameters;
}

