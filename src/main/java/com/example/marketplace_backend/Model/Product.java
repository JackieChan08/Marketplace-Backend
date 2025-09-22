package com.example.marketplace_backend.Model;

import com.example.marketplace_backend.Model.Intermediate_objects.ProductColorImage;
import com.example.marketplace_backend.Model.Intermediate_objects.ProductImage;
import com.example.marketplace_backend.Model.Intermediate_objects.ProductStatuses;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(precision = 15, scale = 2)
    private BigDecimal price;

    @Column(name = "price_description")
    private String priceDescription;

    @Column(name = "discounted_price", precision = 15, scale = 2)
    private BigDecimal discountedPrice;

    @ManyToOne
    @JsonBackReference("product-brand")
    @JoinColumn(name = "brand_id")
    private Brand brand;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductVariant> variants = new ArrayList<>();

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference("product-images")
    private List<ProductImage> images = new ArrayList<>();

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @ManyToOne
    @JsonBackReference("product-subcategory")
    @JoinColumn(name = "subcategory_id")
    private Subcategory subcategory;

    @Column(name = "availability", columnDefinition = "boolean")
    private boolean availability;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference("status-products")
    private Set<ProductStatuses> productStatuses;

    @Column(name = "title")
    private String title;

    @Column(name = "description")
    private String description;
}
