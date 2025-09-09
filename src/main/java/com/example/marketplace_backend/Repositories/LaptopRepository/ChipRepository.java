package com.example.marketplace_backend.Repositories.LaptopRepository;

import com.example.marketplace_backend.Model.ProductSpec.LaptopSpec.Chip;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ChipRepository extends JpaRepository<Chip, UUID> {
}
