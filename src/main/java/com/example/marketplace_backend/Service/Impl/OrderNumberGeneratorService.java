package com.example.marketplace_backend.Service.Impl;

import com.example.marketplace_backend.Model.OrderNumberSequence;
import com.example.marketplace_backend.Repositories.SequenceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class OrderNumberGeneratorService {

    private final SequenceRepository sequenceRepository;

    public String generateOrderNumber() {
        String datePart = LocalDate.now().format(DateTimeFormatter.ofPattern("yyMMdd"));

        // Пробуем найти последовательность для сегодняшнего дня
        OrderNumberSequence sequence = sequenceRepository.findById(datePart).orElse(
                OrderNumberSequence.builder()
                        .datePart(datePart)
                        .sequenceNumber(0L)
                        .build()
        );

        // Увеличиваем счётчик
        long newSequenceNumber = sequence.getSequenceNumber() + 1;
        sequence.setSequenceNumber(newSequenceNumber);
        sequenceRepository.save(sequence);

        return String.format("ORD-%s-%06d", datePart, newSequenceNumber);
    }
}
