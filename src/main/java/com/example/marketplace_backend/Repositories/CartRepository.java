package com.example.marketplace_backend.Repositories;

import com.example.marketplace_backend.Model.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
public interface CartRepository extends JpaRepository<Cart, Long> {
}
