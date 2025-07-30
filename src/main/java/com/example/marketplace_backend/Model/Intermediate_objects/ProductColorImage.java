package com.example.marketplace_backend.Model.Intermediate_objects;

import com.example.marketplace_backend.Model.FileEntity;
import com.example.marketplace_backend.Model.ProductColor;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "product_color_images") // Переименовать таблицу для ясности
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductColorImage {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "color_id", nullable = false)
    @JsonBackReference
    private ProductColor color;

    @ManyToOne
    @JoinColumn(name = "image_id", nullable = false)
    private FileEntity image;
}