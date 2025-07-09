package com.example.marketplace_backend.Repositories;

import com.example.marketplace_backend.Model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<Order, UUID> {
    @Query("SELECT o FROM Order o  ORDER BY o.createdAt DESC")
    List<Order> findAllOrders();

    @Query("SELECT o FROM Order o WHERE o.isWholesale = true ORDER BY o.createdAt DESC")
    List<Order> findAllWholesaleOrders();

    @Query("SELECT o FROM Order o WHERE o.isWholesale = false ORDER BY o.createdAt DESC")
    List<Order> findAllRetailOrders();

    @Query("SELECT o FROM Order o JOIN o.statuses s WHERE s.name = :statusName")
    List<Order> findByStatusesName(@Param("statusName") String statusName);

    @Query("SELECT o FROM Order o WHERE o.id IN (SELECT s.order.id FROM Statuses s WHERE s.name = :statusName)")
    List<Order> findOrdersByStatusName(@Param("statusName") String statusName);

    @Query("SELECT o FROM Order o WHERE o.id IN (SELECT s.order.id FROM Statuses s WHERE s.id = :statusId)")
    List<Order> findOrdersByStatusId(@Param("statusId") UUID statusId);
}
