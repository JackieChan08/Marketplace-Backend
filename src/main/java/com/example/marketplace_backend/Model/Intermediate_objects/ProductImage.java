package com.example.marketplace_backend.Model.Intermediate_objects;

import com.example.marketplace_backend.Model.FileEntity;
import com.example.marketplace_backend.Model.Product;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "product_images")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductImage {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    @JsonManagedReference("product-images")
    private Product product;

    @ManyToOne
    @JoinColumn(name = "image_id", nullable = false)
    private FileEntity image;
}
