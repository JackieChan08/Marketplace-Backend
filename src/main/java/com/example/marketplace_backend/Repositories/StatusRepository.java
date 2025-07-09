package com.example.marketplace_backend.Repositories;

import com.example.marketplace_backend.Model.Statuses;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface StatusRepository extends JpaRepository<Statuses, UUID> {
    Statuses findByName(String name);
}
