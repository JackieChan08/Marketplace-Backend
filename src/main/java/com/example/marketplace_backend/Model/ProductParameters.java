package com.example.marketplace_backend.Model;


import jakarta.persistence.*;
import lombok.Data;

import java.util.List;
@Entity
@Data
@Table(name = "product_parameters")
public class ProductParameters {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @OneToMany(mappedBy = "productParameter", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductSubParameters> subParameters;

    @ManyToOne(fetch = FetchType.LAZY)
    private Product product;
}

