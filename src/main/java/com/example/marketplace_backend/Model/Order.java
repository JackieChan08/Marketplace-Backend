package com.example.marketplace_backend.Model;

import com.example.marketplace_backend.Model.Enums.PaymentMethod;
import com.example.marketplace_backend.Model.Intermediate_objects.OrderItem;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "address", nullable = false)
    private String address;

    @Column(name = "phone_number", nullable = false)
    private String phoneNumber;

    @Column(name = "comment", columnDefinition = "TEXT")
    private String comment;

    @Column(name = "total_price", precision = 15, scale = 2)
    private BigDecimal totalPrice;

    @JsonIgnore
    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<OrderItem> orderItems;

    @Column(name = "is_wholesale")
    private boolean isWholesale;// true — опт, false — розница

    @OneToOne
    @JoinColumn(name = "status_id")
    private Statuses status;

    @Enumerated(EnumType.STRING) // Сохраняет в БД как строку ("CASH" или "TRANSFER")
    @Column(name = "payment_method", nullable = false)
    private PaymentMethod paymentMethod;

    @Column(name = "order_number", unique = true, nullable = false)
    private Long orderNumber;
}

