package com.example.marketplace_backend.Model.Intermediate_objects;

import com.example.marketplace_backend.Model.Category;
import com.example.marketplace_backend.Model.FileEntity;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "category_image")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryImage {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    @JsonBackReference
    private Category category;

    @ManyToOne
    @JoinColumn(name = "image_id", nullable = false)
    private FileEntity image;
}
