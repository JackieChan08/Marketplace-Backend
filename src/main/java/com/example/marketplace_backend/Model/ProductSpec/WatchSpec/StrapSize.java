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
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "watch_spec_id")
    private WatchSpec watchSpec;

    @OneToMany(mappedBy = "strapSize", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Dial> dials;
}
