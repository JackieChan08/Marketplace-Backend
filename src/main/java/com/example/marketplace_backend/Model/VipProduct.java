package com.example.marketplace_backend.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "vip_products")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VipProduct {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column
    private String name;

    @OneToOne
    private FileEntity image;
}
