package com.example.marketplace_backend.Model.Intermediate_objects;


import com.example.marketplace_backend.Model.FileEntity;
import com.example.marketplace_backend.Model.Product;
import jakarta.persistence.*;
import lombok.Data;

import java.security.Timestamp;
import java.util.UUID;

@Entity
@Data
@Table(name = "brand_images")
public class brandImages {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    private Product product;

    @ManyToOne
    private FileEntity image;
}
