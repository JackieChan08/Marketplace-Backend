package com.example.marketplace_backend.Repositories;

import com.example.marketplace_backend.Model.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
}

