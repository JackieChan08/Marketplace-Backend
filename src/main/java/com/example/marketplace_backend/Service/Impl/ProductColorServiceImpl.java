package com.example.marketplace_backend.Service.Impl;

import com.example.marketplace_backend.DTO.Responses.models.ColorResponse;
import com.example.marketplace_backend.DTO.Responses.models.ConnectionResponse;
import com.example.marketplace_backend.DTO.Responses.models.FilteredColorResponseForPhone;
import com.example.marketplace_backend.DTO.Responses.models.MemoryResponse;
import com.example.marketplace_backend.Model.ProductColor;
import com.example.marketplace_backend.Repositories.ProductColorRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ProductColorServiceImpl {
    private final ProductColorRepository productColorRepository;

    public ProductColorServiceImpl(ProductColorRepository productColorRepository) {
        this.productColorRepository = productColorRepository;
    }

    public FilteredColorResponseForPhone filterProduct(
            UUID productId,
            UUID colorId,
            UUID memoryId,
            UUID connectionId
    ) {
        List<ProductColor> colors = productColorRepository.findByProductId(productId);

        // фильтрация по памяти
        if (memoryId != null) {
            colors = colors.stream()
                    .filter(c -> c.getMemoryLinks().stream()
                            .anyMatch(m -> m.getProductMemory().getId().equals(memoryId)))
                    .toList();
        }

        // фильтрация по цвету
        if (colorId != null) {
            colors = colors.stream()
                    .filter(c -> c.getId().equals(colorId))
                    .toList();
        }

        // фильтрация по типу связи
        if (connectionId != null) {
            colors = colors.stream()
                    .filter(c -> c.getConnectionLinks().stream()
                            .anyMatch(conn -> conn.getPhoneConnection().getId().equals(connectionId)))
                    .toList();
        }

        return new FilteredColorResponseForPhone(colors.stream()
                .map(c -> ColorResponse.builder()
                        .id(c.getId())
                        .name(c.getName())
                        .hex(c.getHex())
                        .memories(c.getMemoryLinks().stream()
                                .map(link -> new MemoryResponse(
                                        link.getProductMemory().getId(),
                                        link.getProductMemory().getMemory(),
                                        link.getPrice()
                                ))
                                .toList())
                        .simTypes(c.getConnectionLinks().stream()
                                .map(link -> new ConnectionResponse(
                                        link.getPhoneConnection().getId(),
                                        link.getPhoneConnection().getSimType()
                                ))
                                .toList())
                        .build())
                .toList());
    }
}
