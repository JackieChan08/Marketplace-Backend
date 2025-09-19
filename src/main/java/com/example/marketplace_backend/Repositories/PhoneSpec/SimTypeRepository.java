package com.example.marketplace_backend.Repositories.PhoneSpec;

import com.example.marketplace_backend.Model.ProductSpec.PhoneSpec.SimType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface SimTypeRepository extends JpaRepository<SimType, UUID> {
}
