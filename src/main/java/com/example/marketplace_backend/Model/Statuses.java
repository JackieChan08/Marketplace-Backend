package com.example.marketplace_backend.Model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Entity
@Table(name = "statuses")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Statuses {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column
    private String name;

    @Column
    private String color;

    @ManyToOne
    @JsonBackReference("product-status")
    @JoinColumn(name = "product_id")
    private Product product;

    @ManyToOne
    @JsonBackReference("order-status")
    @JoinColumn(name = "order_id")
    private Order order;
}
