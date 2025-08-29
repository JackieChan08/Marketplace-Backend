package com.example.marketplace_backend.Repositories;

import com.example.marketplace_backend.Model.ProductSpec.PhoneSpec;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PhoneSpecRepository extends JpaRepository<PhoneSpec, UUID> {
}
