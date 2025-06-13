package com.example.marketplace_backend.Model;


import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "subcategory")
public class Subcategory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subcategory_id")
    private Subcategory subcategory;

    private String name;
    private String description;

    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted;
}
