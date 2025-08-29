package com.example.marketplace_backend.Model;

import com.example.marketplace_backend.Model.ProductSpec.LaptopSpec;
import com.example.marketplace_backend.Model.ProductSpec.PhoneSpec;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "product_variants")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductVariant {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(optional = false, cascade = CascadeType.ALL)
    @JoinColumn(name = "product_id")
    private Product product;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "color_id")
    private ProductColor color;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "phone_spec_id")
    private PhoneSpec phoneSpec;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "laptop_spec_id")
    private LaptopSpec laptopSpec;
}
