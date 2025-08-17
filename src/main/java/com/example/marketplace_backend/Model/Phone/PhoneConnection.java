package com.example.marketplace_backend.Model.Phone;

import com.example.marketplace_backend.Model.ProductColor;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "phone_connections")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PhoneConnection {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "sim_type", unique = true)
    private String simType;
}
