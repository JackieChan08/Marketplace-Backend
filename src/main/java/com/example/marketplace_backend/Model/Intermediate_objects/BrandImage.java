package com.example.marketplace_backend.Model.Intermediate_objects;


import com.example.marketplace_backend.Model.Brand;
import com.example.marketplace_backend.Model.FileEntity;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "brand_images")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BrandImage {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "brand_id", nullable = false)
    @JsonBackReference
    private Brand brand;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "image_id", nullable = false)
    private FileEntity image;
}
