package com.example.marketplace_backend.Model.Intermediate_objects;

import com.example.marketplace_backend.Model.Order;
import com.example.marketplace_backend.Model.Statuses;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    @JsonBackReference
    private Order order;

    @ManyToOne
    @JoinColumn(name = "status_id", nullable = false)
    @JsonBackReference
    private Statuses status;
}
