package com.example.marketplace_backend.Repositories;

import com.example.marketplace_backend.Model.Statuses;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface StatusRepository extends JpaRepository<Statuses, UUID> {

    @Query("SELECT s FROM Statuses s WHERE s.name = :name")
    Optional<Statuses> findByName(@Param("name") String name);

    @Query("SELECT s FROM Statuses s WHERE s.orderFlag = true ")
    List<Statuses> findAllByOrderFlag();

    @Query("SELECT s FROM Statuses s WHERE s.productFlag = true ")
    List<Statuses> findAllByProductFlag();
}