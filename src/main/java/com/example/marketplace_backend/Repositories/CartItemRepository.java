package com.example.marketplace_backend.Repositories;

import com.example.marketplace_backend.Model.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CartItemRepository extends JpaRepository<CartItem, UUID> {
}

