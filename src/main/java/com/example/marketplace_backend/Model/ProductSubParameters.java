package com.example.marketplace_backend.Model;


import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "product_sub_parameters")
public class ProductSubParameters {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String value;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_parameter_id")
    private ProductParameters productParameter;
}
