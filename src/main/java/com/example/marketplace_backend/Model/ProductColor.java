package com.example.marketplace_backend.Model;

import com.example.marketplace_backend.Model.Intermediate_objects.ProductColorImage;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "product_colors")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductColor {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column
    private String name;

    @Column
    private String hex;

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "product_id")
    private Product product;

    @OneToMany(mappedBy = "color", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<ProductColorImage> images = new ArrayList<>();;

    @OneToMany(mappedBy = "color", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<ProductMemory> memories = new ArrayList<>();
}
