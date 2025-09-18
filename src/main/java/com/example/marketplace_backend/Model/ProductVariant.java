package com.example.marketplace_backend.Model;

import com.example.marketplace_backend.Model.ProductSpec.LaptopSpec.LaptopSpec;
import com.example.marketplace_backend.Model.ProductSpec.PhoneSpec;
import com.example.marketplace_backend.Model.ProductSpec.TableSpec.TableSpec;
import com.example.marketplace_backend.Model.ProductSpec.WatchSpec.WatchSpec;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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

    @ManyToOne(optional = false)
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

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "table_spec_id")
    private TableSpec tableSpec;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "watch_spec_id")
    private WatchSpec watchSpec;
}
