package com.example.marketplace_backend.Model.Phone;

import com.example.marketplace_backend.Model.ProductColor;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "phone_connection_and_product_color")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PhoneConnectionAndProductColor {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "color_id", nullable = false)
    private ProductColor productColor;

    @ManyToOne
    @JoinColumn(name = "connection_id", nullable = false)
    private PhoneConnection phoneConnection;
}