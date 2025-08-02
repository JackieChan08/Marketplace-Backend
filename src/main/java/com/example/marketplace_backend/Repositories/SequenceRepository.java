package com.example.marketplace_backend.Repositories;

import com.example.marketplace_backend.Model.DummyEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface SequenceRepository extends JpaRepository<DummyEntity, UUID> {

    @Query(value = "SELECT nextval('order_number_seq')", nativeQuery = true)
    Long getNextOrderNumber();
}
