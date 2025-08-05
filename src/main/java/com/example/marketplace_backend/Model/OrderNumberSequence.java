package com.example.marketplace_backend.Model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "order_number_sequence")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderNumberSequence {

    @Id
    private String datePart; // формат YYMMDD, например "250805"

    @Column(nullable = false)
    private Long sequenceNumber;
}
