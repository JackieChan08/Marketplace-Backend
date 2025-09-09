package com.example.marketplace_backend.Repositories.WatchRepository;

import com.example.marketplace_backend.Model.ProductSpec.WatchSpec.Dial;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface DialRepository extends JpaRepository<Dial, UUID> {
}
