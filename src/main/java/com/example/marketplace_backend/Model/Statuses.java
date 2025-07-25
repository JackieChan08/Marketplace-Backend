package com.example.marketplace_backend.Model;

import com.example.marketplace_backend.Model.Intermediate_objects.CartItem;
import com.example.marketplace_backend.Model.Intermediate_objects.OrderStatuses;
import com.example.marketplace_backend.Model.Intermediate_objects.ProductStatuses;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
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

    @Column(name = "primary_color")
    private String primaryColor;

    @Column(name = "background_color")
    private String backgroundColor;

    @Column(name = "order_flag")
    private boolean orderFlag;

    @Column(name = "product_flag")
    private boolean productFlag;

    @OneToMany(mappedBy = "status", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference("status-products")
    private List<ProductStatuses> productStatuses = new ArrayList<>();

    @OneToMany(mappedBy = "status", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference("status-orders")
    private List<OrderStatuses> orderStatuses = new ArrayList<>();
}