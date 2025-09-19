package com.example.marketplace_backend.Model.ProductSpec.PhoneSpec;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "sim_type")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SimType {
    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false, length = 30)
    private String name;
}
