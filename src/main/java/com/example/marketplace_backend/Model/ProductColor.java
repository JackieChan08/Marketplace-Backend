package com.example.marketplace_backend.Model;

import com.example.marketplace_backend.Model.Intermediate_objects.ProductColorImage;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Where;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "product_colors")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Where(clause = "deleted_at IS NULL")
public class ProductColor {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column
    private String name;

    @Column
    private String hex;

    @Column
    private BigDecimal price;

    @OneToMany(mappedBy = "color", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<ProductColorImage> images = new ArrayList<>();

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;
}