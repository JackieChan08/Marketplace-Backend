package com.example.marketplace_backend.Model.ProductSpec.PhoneSpec;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "phone_spec")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PhoneSpec {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "sim_type", nullable = false)
    private SimType simType;

    @ManyToOne(optional = false)
    @JoinColumn(name = "phone_memory_id", nullable = false)
    private PhoneMemory phoneMemory;
}
