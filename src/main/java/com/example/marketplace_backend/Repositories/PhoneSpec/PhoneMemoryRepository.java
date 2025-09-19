package com.example.marketplace_backend.Repositories.PhoneSpec;

import com.example.marketplace_backend.Model.ProductSpec.PhoneSpec.PhoneMemory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PhoneMemoryRepository extends JpaRepository<PhoneMemory, UUID> {
}
