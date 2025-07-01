package com.example.marketplace_backend.Repositories;

import com.example.marketplace_backend.Model.Intermediate_objects.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, UUID> {
}

