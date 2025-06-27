package com.example.marketplace_backend.Model.Intermediate_objects;

import com.example.marketplace_backend.Model.FileEntity;
import com.example.marketplace_backend.Model.Subcategory;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "subcategory_images")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubcategoryImage {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "subcategory_id", nullable = false)
    private Subcategory subcategory;

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "image_id")
    private FileEntity image;
}
