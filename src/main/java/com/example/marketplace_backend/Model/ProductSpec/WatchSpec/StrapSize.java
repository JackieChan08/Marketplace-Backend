package com.example.marketplace_backend.Model.ProductSpec.WatchSpec;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "strap_sizes")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StrapSize {
    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false, length = 30)
    private String name;
}
