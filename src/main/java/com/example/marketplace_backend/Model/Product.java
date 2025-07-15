package com.example.marketplace_backend.Model;


import com.example.marketplace_backend.Model.Intermediate_objects.ProductImage;
import com.example.marketplace_backend.Model.Intermediate_objects.ProductStatuses;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.naming.Name;
import java.math.BigDecimal;
import java.time.LocalDateTime;
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

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal price;

    @Column(name = "discounted_price", precision = 15, scale = 2)
    private BigDecimal discountedPrice;

    @ManyToOne
    @JsonBackReference("product-brand")
    @JoinColumn(name = "brand_id")
    private Brand brand;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference("product-images")
    private List<ProductImage> productImages;

    // Закомментированный старый код:
    // @JoinTable(
    //         name = "product_images",
    //         joinColumns = @JoinColumn(name = "product_id"),
    //         inverseJoinColumns = @JoinColumn(name = "image_id")
    // )
    // private List<FileEntity> images;

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

    @Column(name = "availability", columnDefinition = "true")
    private boolean availability; // true - в наличии, false - не в наличии

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference("product-statuses")
    private Set<ProductStatuses> productStatuses;

    @Column(name = "title")
    private String title;

    @Column(name = "description")
    private String description;
}