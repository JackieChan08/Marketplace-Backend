package com.example.marketplace_backend.Model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "product_sub_parameters")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductSubParameters {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String value;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_parameter_id", nullable = false)
    private ProductParameters productParameter;
}
