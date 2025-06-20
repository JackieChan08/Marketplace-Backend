package com.example.marketplace_backend.Model;


import com.example.marketplace_backend.Model.Intermediate_objects.BrandImage;
import com.example.marketplace_backend.Model.Intermediate_objects.CategoryImage;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.security.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Data
@Table(name = "brand")
@NoArgsConstructor
@AllArgsConstructor
@Builder
//для софт delete
//@SQLDelete(sql = "UPDATE brand SET deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
//@Where(clause = "deleted_at IS NULL")
public class Brand {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(unique = true, nullable = false)
    private String name;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @OneToMany(mappedBy = "brand", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Product> products;

    @OneToMany(mappedBy = "brand", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<BrandImage> brandImages;
//    Пример без класса BrandImage
//    @JoinTable(
//            name = "brand_images",
//            joinColumns = @JoinColumn(name = "brand_id"),
//            inverseJoinColumns = @JoinColumn(name = "image_id")
//    )
//    private List<FileEntity> images;


}
