package com.example.marketplace_backend.Repositories;

import com.example.marketplace_backend.Model.Order;
import com.example.marketplace_backend.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByLogin(String login);
    @Query("SELECT o FROM Order o WHERE o.user = :user ")
    List<Order> ordersByUser(@Param("user") User user);

    Optional<User> findByEmail(String email);
}
