package com.example.marketplace_backend.Repositories;

import com.example.marketplace_backend.Model.Order;
import com.example.marketplace_backend.Model.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    boolean existsByEmail(String email);
    Optional<User> findByEmail(String email);

    @Query("SELECT o FROM Order o WHERE o.user = :user ")
    List<Order> ordersByUser(@Param("user") User user);

    @Query("""
    SELECT u FROM User u
    WHERE LOWER(u.name) LIKE LOWER(CONCAT('%', :query, '%'))
       OR LOWER(u.email) LIKE LOWER(CONCAT('%', :query, '%'))
       OR LOWER(u.phoneNumber) LIKE LOWER(CONCAT('%', :query, '%'))
    """)
    Page<User> searchUsers(@Param("query") String query, Pageable pageable);

}
