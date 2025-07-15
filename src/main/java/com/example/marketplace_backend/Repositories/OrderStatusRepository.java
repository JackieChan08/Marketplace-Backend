package com.example.marketplace_backend.Repositories;

import com.example.marketplace_backend.Model.Intermediate_objects.OrderStatuses;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrderStatusRepository extends JpaRepository<OrderStatuses, UUID> {
     Optional<OrderStatuses> findFirstByOrderIdOrderByIdDesc(UUID orderId);
     List<OrderStatuses> findByOrderIdOrderByIdAsc(UUID orderId);
}
