package com.example.marketplace_backend.Model.ProductSpec;

import com.example.marketplace_backend.Model.ProductColor;
import com.example.marketplace_backend.enums.PaymentMethod;
import com.example.marketplace_backend.enums.SimType;
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

    @Column(nullable = false)
    private String memory;

    @Column(nullable = false)
    private BigDecimal price;

    @Enumerated(EnumType.STRING)
    @Column(name = "sim_type", nullable = false)
    private SimType simType;
}
