package com.example.marketplace_backend.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "order_statuses")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderStatuses {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    UUID id;

    @Column(name = "name")
    String name;

    @Column(name = "color")
    String color;

    @OneToMany(mappedBy = "orderStatuses", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Order> orders;
}
