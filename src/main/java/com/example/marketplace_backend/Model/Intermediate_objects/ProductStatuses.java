package com.example.marketplace_backend.Model.Intermediate_objects;

import com.example.marketplace_backend.Model.Product;
import com.example.marketplace_backend.Model.Statuses;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "product_statuses")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductStatuses {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    @JsonBackReference("status-products")
    private Product product;

    @ManyToOne
    @JoinColumn(name = "status_id", nullable = false)
    @JsonBackReference
    private Statuses status;
}
