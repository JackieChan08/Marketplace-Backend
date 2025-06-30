package com.example.marketplace_backend.Model.Intermediate_objects;

import com.example.marketplace_backend.Model.Favorite;
import com.example.marketplace_backend.Model.Product;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "favorite_item")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FavoriteItem {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private int quantity;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal price;

    @CreationTimestamp
    @Column(name = "added_at", nullable = false)
    private LocalDateTime addedAt;

    @ManyToOne
    @JoinColumn(name = "favorite_id", nullable = false)
    private Favorite favorite;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;
}
