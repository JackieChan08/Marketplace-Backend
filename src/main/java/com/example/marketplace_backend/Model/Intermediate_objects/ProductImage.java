package com.example.marketplace_backend.Model.Intermediate_objects;


import com.example.marketplace_backend.Model.FileEntity;
import com.example.marketplace_backend.Model.Product;
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
    private Product product;

    @ManyToOne
    private FileEntity image;
}
