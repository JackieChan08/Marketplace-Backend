package com.example.marketplace_backend.Repositories;

import com.example.marketplace_backend.Model.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CartRepository extends JpaRepository<Cart, UUID> {
}
