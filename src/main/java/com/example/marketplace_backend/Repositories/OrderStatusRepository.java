package com.example.marketplace_backend.Repositories;

import com.example.marketplace_backend.Model.OrderStatuses;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrderStatusRepository extends JpaRepository<OrderStatuses, UUID> {
    Optional<OrderStatuses> findByName(String name);

    boolean existsByName(String name);

    @Query("SELECT os FROM OrderStatuses os WHERE LOWER(os.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<OrderStatuses> findByNameContainingIgnoreCase(@Param("name") String name);
}
