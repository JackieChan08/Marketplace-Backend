package com.example.marketplace_backend.Repositories;

import com.example.marketplace_backend.Model.OrderNumberSequence;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SequenceRepository extends JpaRepository<OrderNumberSequence, String> {
}
