package com.example.marketplace_backend.Model;


import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;

import java.util.Set;

@Entity
@Data
@Table(name = "products")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private double price;
    private String description;

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "subcategory_id")
    private Subcategory subcategory;
    @JsonBackReference
    @Column(name = "image", columnDefinition = "LONGTEXT")
    private String image;

    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted;



    public Product() {

    }
}
