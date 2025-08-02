package com.example.marketplace_backend.Service.Impl;

import com.example.marketplace_backend.Repositories.SequenceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderNumberGeneratorService {

    private final SequenceRepository sequenceRepository;

    public Long generateOrderNumber() {
        return sequenceRepository.getNextOrderNumber();
    }
}


