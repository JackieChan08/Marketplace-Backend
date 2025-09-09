package com.example.marketplace_backend.Repositories.WatchRepository;

import com.example.marketplace_backend.Model.ProductSpec.WatchSpec.WatchSpec;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface WatchSpecRepository extends JpaRepository<WatchSpec, UUID> {
}
